package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.itxtech.mcl.Loader;
import org.itxtech.mcl.component.DownloadObserver;
import org.itxtech.mcl.component.Downloader;
import org.itxtech.mcl.module.MclModule;

import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2022 iTX Technologies
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author PeratX
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public class MDownloader extends MclModule {
    private static final String MAX_THREADS_KEY = "mdownloader.max-threads";

    @Override
    public String getName() {
        return "mdownloader";
    }

    @Override
    public void prepare() {
        loader.options.addOption(Option.builder().desc("Set Max Threads of Multithreading Downloader")
                .longOpt("set-max-threads").hasArg().argName("MaxThreads").build());
    }

    @Override
    public void cli() {
        if (loader.cli.hasOption("set-max-threads")) {
            try {
                var t = loader.cli.getOptionValue("set-max-threads");
                Integer.parseInt(t);
                loader.config.moduleProps.put(MAX_THREADS_KEY, t);
            } catch (Exception ignored) {
                loader.logger.error("Invalid Max Threads value");
            }
        }
        loader.downloader = new MultithreadingDownloaderImpl(loader.downloader,
                Integer.parseInt(loader.config.moduleProps.getOrDefault(MAX_THREADS_KEY, "16")));
    }

    public static class MultithreadingDownloaderImpl implements Downloader {
        private static final int MIN_SIZE = 2 * 1024 * 1024; // < 2MB

        private int maxThreads;
        private Downloader defaultDownloader;

        public MultithreadingDownloaderImpl(Downloader defaultDownloader, int maxThreads) {
            this.maxThreads = maxThreads;
            this.defaultDownloader = defaultDownloader;
        }

        @Override
        public void download(String url, File file, DownloadObserver observer) {
            var loader = Loader.getInstance();
            try {
                var header = loader.repo.httpHead(url);
                var len = header.headers().firstValueAsLong("Content-Length").getAsLong();
                if (len < MIN_SIZE) {
                    defaultDownloader.download(url, file, observer);
                } else {
                    var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
                    var start = 0L;
                    var end = 0L;
                    var part = len / maxThreads;
                    var list = new ArrayList<DownloadTask>();
                    while (start < len) {
                        end = Math.min(len - 1, start + part);
                        var task = new DownloadTask(start, end, url);
                        list.add(task);
                        executor.submit(task);
                        start = end + 1;
                    }
                    while (executor.getActiveCount() > 0) {
                        var sum = 0;
                        for (var task : list) {
                            sum += task.read;
                        }
                        if (observer != null) {
                            observer.updateProgress((int) len, sum);
                        }
                        Thread.sleep(100);
                    }
                    var os = new BufferedOutputStream(new FileOutputStream(file));
                    for (var task : list) {
                        os.write(task.out.toByteArray());
                    }
                    os.flush();
                    os.close();
                    executor.shutdownNow();
                }
            } catch (Exception e) {
                loader.logger.error(e);
            }
        }
    }

    private static class DownloadTask implements Runnable {
        private long start;
        private long end;
        private long contentLen;
        private String url;
        public long read;
        public ByteArrayOutputStream out;

        public DownloadTask(long start, long end, String url) {
            this.start = start;
            this.end = end;
            contentLen = end - start + 1;
            this.url = url;
            this.read = 0;
            out = new ByteArrayOutputStream();
        }

        @Override
        public void run() {
            try {
                var proxy = Loader.getInstance().getProxy();
                var connection = proxy == null ? new URL(url).openConnection() :
                        new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, proxy));
                connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
                connection.connect();
                var is = new BufferedInputStream(connection.getInputStream());
                var os = new BufferedOutputStream(out);
                var len = 0;
                var buff = new byte[1024];
                while (read < contentLen && (len = is.read(buff)) != -1) {
                    os.write(buff, 0, len);
                    read += len;
                }
                os.flush();
                os.close();
                is.close();
            } catch (Throwable e) {
                Loader.getInstance().logger.logException(e);
            }
        }
    }
}
