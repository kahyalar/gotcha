/**
 * Copyright 2017, Google, Inc.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kahyalar.selenium;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class Detect {
    private static final String TARGET_URL =
            "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
            "key=AIzaSyAgvdOgmG-vrJF4Lvw5GjlkvfZO3DScNrY";
    /**
     * Detects entities,sentiment and syntax in a document using the Natural Language API.
     *
     * @throws IOException on Input/Output errors.
     */
    public static void main(String[] args) throws IOException {
        argsHelper(args, System.out);
    }

    /**
     * Helper that handles the input passed to the program.
     *
     * @throws IOException on Input/Output errors.
     */
    public static void argsHelper(String[] args, PrintStream out) throws IOException {
        if (args.length < 1) {
            out.println("Usage:");
            out.printf(
                    "\tjava %s \"<command>\" \"<path-to-image>\"\n"
                            + "Commands:\n"
                            + "\tall-local | faces | labels | landmarks | logos | text | safe-search | properties"
                            + "| web | crop \n"
                            + "Path:\n\tA file path (ex: ./resources/wakeupcat.jpg) or a URI for a Cloud Storage "
                            + "resource (gs://...)\n",
                    Detect.class.getCanonicalName());
            return;
        }
        String command = args[0];
        String path = args.length > 1 ? args[1] : "";

        Detect app = new Detect(ImageAnnotatorClient.create());
        if (command.equals("all-local")) {
            detectText("resources/text.jpg", out);
        } else {
            detectText(path, out);
        }
    }

    private static ImageAnnotatorClient visionApi;

    /**
     * Constructs a {@link Detect} which connects to the Cloud Vision API.
     *
     * @param client The Vision API client.
     */
    public Detect(ImageAnnotatorClient client) throws IOException {
        visionApi = client;
    }

    /**
     * Detects text in the specified image.
     *
     * @param filePath The path to the file to detect text in.
     * @param out A {@link PrintStream} to write the detected text to.
     * @throws IOException on Input/Output errors.
     */
    public static void detectText(String filePath, PrintStream out) throws IOException {
        URL serverUrl = new URL(TARGET_URL + API_KEY);
        URLConnection urlConnection = serverUrl.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setDoOutput(true);
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
        byte[] temp = imgBytes.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        String imageRequest = encoder.encode(temp).toString();
        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()));
        httpRequestBodyWriter.write
                ("{\n" +
                        "  \"requests\": [\n" +
                        "    {\n" +
                        "      \"images\": {\n" +
                        "        \"content\": \""+imageRequest+ "\"\n " +
                        "      },\n" +
                        "      \"features\": [\n" +
                        "        {\n" +
                        "          \"type\": \"TEXT_DETECTION\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");
        httpRequestBodyWriter.close();

        String response = httpConnection.getResponseMessage();

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);


        //BatchAnnotateImagesResponse response = visionApi.batchAnnotateImages(requests);
        //List<AnnotateImageResponse> responses = response.getResponsesList();

        if (httpConnection.getInputStream() == null) {
            System.out.println("No stream");
            return;
        }

        Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
            resp += line;
            System.out.println(line);  //  alternatively, print the line of response
        }
        httpResponseScanner.close();

    }
}
