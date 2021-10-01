package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpUtil {
    public static final ListeningExecutorService field_180193_a = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Downloader %d").build()));

    /**
     * The number of download threads that we have started so far.
     */
    private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();

    /**
     * Builds an encoded HTTP POST content string from a string map
     */
    public static String buildPostString(Map<String, Object> data) {
        StringBuilder stringbuilder = new StringBuilder();

        for (Entry<String, Object> entry : data.entrySet()) {
            if (stringbuilder.length() > 0) {
                stringbuilder.append('&');
            }

            try {
                stringbuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException unsupportedencodingexception1) {
                unsupportedencodingexception1.printStackTrace();
            }

            if (entry.getValue() != null) {
                stringbuilder.append('=');

                try {
                    stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException unsupportedencodingexception) {
                    unsupportedencodingexception.printStackTrace();
                }
            }
        }

        return stringbuilder.toString();
    }

    /**
     * Sends a POST to the given URL using the map as the POST args
     */
    public static String postMap(URL url, Map<String, Object> data, boolean skipLoggingErrors) {
        return post(url, buildPostString(data), skipLoggingErrors);
    }

    /**
     * Sends a POST to the given URL
     */
    private static String post(URL url, String content, boolean skipLoggingErrors) {
        try {
            Proxy proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

            if (proxy == null) {
                proxy = Proxy.NO_PROXY;
            }

            HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection(proxy);
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpurlconnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());
            dataoutputstream.writeBytes(content);
            dataoutputstream.flush();
            dataoutputstream.close();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuilder stringbuffer = new StringBuilder();
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                stringbuffer.append(s);
                stringbuffer.append('\r');
            }

            bufferedreader.close();
            return stringbuffer.toString();
        } catch (Exception exception) {
            if (!skipLoggingErrors) {
                logger.error("Could not post to " + url, exception);
            }

            return "";
        }
    }

    public static ListenableFuture<Object> downloadResourcePack(final File saveFile, final String packUrl, final Map<String, String> packList, final int maxSize, final IProgressUpdate progress, final Proxy proxy) {
        ListenableFuture<?> listenablefuture = field_180193_a.submit(() -> {
            HttpURLConnection httpurlconnection = null;
            InputStream inputstream = null;
            OutputStream outputstream = null;

            if (progress != null) {
                progress.resetProgressAndMessage("Downloading Resource Pack");
                progress.displayLoadingString("Making Request...");
            }

            try {
                try {
                    byte[] buffer = new byte[4096];
                    URL url = new URL(packUrl);
                    httpurlconnection = (HttpURLConnection) url.openConnection(proxy);
                    float f = 0.0F;
                    float f1 = (float) packList.entrySet().size();

                    for (Entry<String, String> entry : packList.entrySet()) {
                        httpurlconnection.setRequestProperty(entry.getKey(), entry.getValue());

                        if (progress != null) {
                            progress.setLoadingProgress((int) (++f / f1 * 100.0F));
                        }
                    }

                    inputstream = httpurlconnection.getInputStream();
                    f1 = (float) httpurlconnection.getContentLength();
                    int i = httpurlconnection.getContentLength();

                    if (progress != null) {
                        progress.displayLoadingString(String.format("Downloading file (%.2f MB)...", f1 / 1000.0F / 1000.0F));
                    }

                    if (saveFile.exists()) {
                        long j = saveFile.length();

                        if (j == (long) i) {
                            if (progress != null) {
                                progress.setDoneWorking();
                            }

                            return;
                        }

                        HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + i + " vs our " + j + ").");
                        FileUtils.deleteQuietly(saveFile);
                    } else if (saveFile.getParentFile() != null) {
                        saveFile.getParentFile().mkdirs();
                    }

                    outputstream = new DataOutputStream(new FileOutputStream(saveFile));

                    if (maxSize > 0 && f1 > (float) maxSize) {
                        if (progress != null) {
                            progress.setDoneWorking();
                        }

                        throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + maxSize + ")");
                    }

                    int k = 0;

                    while ((k = inputstream.read(buffer)) >= 0) {
                        f += (float) k;

                        if (progress != null) {
                            progress.setLoadingProgress((int) (f / f1 * 100.0F));
                        }

                        if (maxSize > 0 && f > (float) maxSize) {
                            if (progress != null) {
                                progress.setDoneWorking();
                            }

                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + maxSize + ")");
                        }

                        if (Thread.interrupted()) {
                            HttpUtil.logger.error("INTERRUPTED");

                            if (progress != null) {
                                progress.setDoneWorking();
                            }

                            return;
                        }

                        outputstream.write(buffer, 0, k);
                    }

                    if (progress != null) {
                        progress.setDoneWorking();
                        return;
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();

                    if (httpurlconnection != null) {
                        InputStream inputstream1 = httpurlconnection.getErrorStream();

                        try {
                            HttpUtil.logger.error(IOUtils.toString(inputstream1));
                        } catch (IOException ioexception) {
                            ioexception.printStackTrace();
                        }
                    }

                    if (progress != null) {
                        progress.setDoneWorking();
                        return;
                    }
                }
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(outputstream);
            }
        });
        return (ListenableFuture<Object>) listenablefuture;
    }

    public static int getSuitableLanPort() throws IOException {
        ServerSocket serversocket = null;
        int i = -1;

        try {
            serversocket = new ServerSocket(0);
            i = serversocket.getLocalPort();
        } finally {
            try {
                if (serversocket != null) {
                    serversocket.close();
                }
            } catch (IOException var8) {
            }
        }

        return i;
    }

    /**
     * Send a GET request to the given URL.
     */
    public static String get(URL url) throws IOException {
        HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setRequestMethod("GET");
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
        StringBuilder stringbuilder = new StringBuilder();
        String s;

        while ((s = bufferedreader.readLine()) != null) {
            stringbuilder.append(s);
            stringbuilder.append('\r');
        }

        bufferedreader.close();
        return stringbuilder.toString();
    }
}
