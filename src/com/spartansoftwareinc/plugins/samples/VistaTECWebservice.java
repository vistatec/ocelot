package com.spartansoftwareinc.plugins.samples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spartansoftwareinc.plugins.Plugin;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import java.util.List;

/**
 * Plugin for sending ITS metadata to the VistaTEC Web Service.
 */
public class VistaTECWebservice implements Plugin {
    private static Logger LOG = LoggerFactory.getLogger(VistaTECWebservice.class);
    private String webservice_request_url = "https://vtcoreservices.vistatec.ie/api/its20/";

    @Override
    public String getPluginName() {
        return "VistaTEC Webservice";
    }

    @Override
    public String getPluginVersion() {
        return "1.0";
    }

    @Override
    public void sendLQIData(String sourceLang, String targetLang,
            Segment seg, List<LanguageQualityIssue> lqiList) {
        VistaTECLQI vLQI;
        for (LanguageQualityIssue lqi : lqiList) {
            try {
                vLQI = new VistaTECLQI(seg.getFileOriginal(),
                        seg.getTransUnitId(), "", sourceLang, targetLang,
                        seg.getSource().toString(), seg.getTarget().toString(),
                        lqi.getType(), lqi.getSeverity(), lqi.getComment());
                postLQIData(vLQI);
            } catch (IOException ex) {
                LOG.error("Send LQI Exception", ex);
            }
        }
    }

    public void postProvData(VistaTECProvenance vProv) throws IOException {
        try {
            URL url = new URL(webservice_request_url + "provenance");
            System.out.println("Sending metadata: "+JsonFormatter.toJson(vProv, true));
            InputStream response = initiateHttpPostJSONRequest(url, JsonFormatter.toJson(vProv, false));
            if (response != null) {
                InputStreamReader inReader = new InputStreamReader(response);
                BufferedReader reader = new BufferedReader(inReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Received response: "+line);
                }
            }
        } catch (MalformedURLException e) {
            LOG.error("Invalid URL: " + webservice_request_url, e);
        }
    }

    @Override
    public void sendProvData(String sourceLang, String targetLang,
            Segment seg, List<Provenance> provList) {
        VistaTECProvenance vProv;
        for (Provenance prov : provList) {
            try {
                vProv = new VistaTECProvenance(seg.getFileOriginal(),
                        seg.getTransUnitId(), "", sourceLang, targetLang,
                        seg.getSource().toString(), seg.getTarget().toString(),
                        prov.getPerson(), prov.getOrg(), prov.getTool(),
                        prov.getRevPerson(), prov.getRevOrg(), prov.getRevTool());
                postProvData(vProv);
            } catch (IOException ex) {
                LOG.error("Send Prov Exception", ex);
            }
        }
    }

    public void postLQIData(VistaTECLQI vLQI) throws IOException {
        try {
            URL url = new URL(webservice_request_url + "QualityIssue");
            System.out.println("Sending metadata: "+JsonFormatter.toJson(vLQI, true));
            InputStream response = initiateHttpPostJSONRequest(url, JsonFormatter.toJson(vLQI, false));
            if (response != null) {
                InputStreamReader inReader = new InputStreamReader(response);
                BufferedReader reader = new BufferedReader(inReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Received response: "+line);
                }
            }
        } catch (MalformedURLException e) {
            LOG.error("Invalid URL: " + webservice_request_url, e);
        }
    }

    public InputStream initiateHttpPostJSONRequest(URL url, String json) {
        for (int i = 0; i < 3; i++) {
            HttpURLConnection http_request;
            try {
                http_request = (HttpURLConnection) url.openConnection();
                http_request.setDoOutput(true);
                http_request.setRequestMethod("POST");
                http_request.setRequestProperty("Content-Type", "application/json");
                http_request.setRequestProperty("Accept", "application/json");
                http_request.setRequestProperty("Authorization", "Basic cGhpbHI6cGFzc3dvcmQ=");
                Writer out = new BufferedWriter(new OutputStreamWriter(http_request.getOutputStream()));
                out.write(json);
                out.close();

                InputStream response = http_request.getInputStream();
                if (http_request.getResponseCode() >= 200 && http_request.getResponseCode() < 300) {
                    return response;
                } else if (http_request.getResponseCode() >= 500 && http_request.getResponseCode() < 600) {
                    LOG.error("Got system error; retry #" + (i + 1));
                } else {
                    throw new IllegalStateException("Http post request failed: " + http_request.getResponseCode());
                }
            } catch (IOException e) {
                LOG.error("Got error; retry #" + (i + 1), e);
                continue;
            }
        }
        return null;
    }
}
