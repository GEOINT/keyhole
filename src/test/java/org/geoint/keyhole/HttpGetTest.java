/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.keyhole;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests HTTP GET requests using Embedded Glassfish
 */
@RunWith(Arquillian.class)
public class HttpGetTest {

    private static final String FILTER_PROP_TEST_CONFIG = "dataUrl";
    private static final String FILTER_PROP_TEST_CONFIG_FILE
            = "/WEB-INF/classes/net/sf/j2ep/test/testData.xml";
    private static final Logger logger
            = Logger.getLogger(HttpGetTest.class.getName());
    private static final Map<String, String> writtenUrls = new HashMap<>();

    /**
     * Console display of proxy rewrites by regex group. Facilitates regex
     * debugging.
     */
    @AfterClass
    public static void reportProxyURLRewrites() {
        System.out.println("\n\n rewrites summary:\n");
        for (int i = 0; i < 7; i++) {
            System.out.println("\ngroup" + i + "\n\n");
            for (Entry<String, String> e : writtenUrls.entrySet()) {
                if (e.getKey().endsWith(Integer.toString(i))) {
                    System.out.println(e.getKey() + " : " + e.getValue());
                }
            }
            System.out.println("\n\n\n");
        }
    }

    /**
     *
     * @return ShrinkWrap bundled test jar
     */
    @Deployment
    @OverProtocol("Servlet 3.0")
    public static Archive<?> init() {
        return ShrinkWrap.create(WebArchive.class, "keyhole.war")
                .addPackage(".net.sf.j2ep")
                .addAsWebInfResource(
                        new File("src/test/resources/WEB-INF/web.xml"))
                .addAsWebResource(
                        new File("src/test/resources/WEB-INF/testData.xml"));
    }

    @ArquillianResource
    URL baseUrl;

    /**
     *
     * @throws Exception
     */
    @RunAsClient
    @Test
    public void testNothing() throws Exception {

        assertTrue(true);
    }

    /**
     * this test requires an instance of Jenkins to be running separate from the
     * Arquillian/Shrinkwrap embedded war containing glassfish
     *
     * @throws Exception
     */
    @Ignore
    @RunAsClient
    @Test
    public void testJenkinsExtension() throws Exception {
        URL url = new URL(baseUrl.toString() + "jenkins");
        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
        logger.log(
                Level.INFO, "cnx response code: {0}\n", cnx.getResponseCode());
        assertEquals(200, cnx.getResponseCode());

    }

    /**
     * verifies that the regex used to extract links from html tags functions
     * properly
     *
     * @throws Exception
     */
    @RunAsClient
    @Test
    public void testRegexGroups() throws Exception {
        logger.info("testRegexGroups");
        String regex
                = "\\b(href=|src=|action=|url=)([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']";
        Pattern testRegexPattern
                = Pattern.compile(regex,
                        Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
        Matcher testMatcher
                = testRegexPattern.matcher("<link rel='stylesheet' "
                        + "href='/jenkins/static/4fe0e213/css/style.css' "
                        + "type='text/css' />");
        StringBuffer sb = new StringBuffer();
        if (testMatcher.find()) {
            StringBuilder stringBldr = new StringBuilder();
            stringBldr.append(testMatcher.group(1))
                    .append(testMatcher.group(2))
                    .append("/keyhole")
                    .append(testMatcher.group(6));

            testMatcher.appendReplacement(sb, stringBldr.toString());
            testMatcher.appendTail(sb);
        }
        assertTrue(true);
    }

    /**
     * early initial regex test
     *
     * @throws Exception
     */
    @RunAsClient
    @Test
    public void testHtmlLinksRegex() throws Exception {

        Pattern htmlLinksPattern
                = Pattern.compile(
                        "\\b(href=|src=|action=|url=\\()([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']",
                        Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
        Matcher htmlLinksMatcher
                = htmlLinksPattern.matcher(
                        "<script src=\"/jenkins/static/4fe0e213/scripts/yui/yahoo/yahoo-min.js\">");

        assertTrue(htmlLinksMatcher.find());

    }

    /*
     *
     *
     * Regular expression tests.  The matchers found in the tests below are 
     * built from html tags with urls embedded as tag attributes.  The tags have
     * been extracted from the Jenkins Dashboard and the Glassfish Admin 
     * console, as the refactoring of j2ep was undertaken to specifically enable
     * us to access these applications through a proxy.  
     *
     *
     */
    /**
     * The base regex pattern used by the J2EP proxy rewriter.
     */
    Pattern testPattern
            = Pattern.compile(
                    "\\b(href=|src=|action=|url[(])([\"\']?)(([^/]+://)([^/<>]+))?([^\"\'>\\)]*)([\"\']?)",
                    Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);

    
      //original regular expressions kept for reference
    
//    String baseRegex = "\\b(href=|src=|action=|url)([\"\'])?(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']";
//    String originalRegex = "\\b(href=|src=|action=|url\\()([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']";
//    Pattern testPattern = Pattern.compile("\\b(href=|src=|action=|url\\()([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']", 
//    Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
    
    
    
    /**
     * jenkinsBaseMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsBaseMatcher() {
        logger.info("\n\n\n jenkinsBaseMatcher \n");
        Matcher jenkinsBaseMatcher
                = testPattern.matcher("<link rel='stylesheet' "
                        + "href='/jenkins/static/4fe0e213/css/style.css' "
                        + "type='text/css' />");
        if (jenkinsBaseMatcher.find()) {
            for (int i = 0; i < jenkinsBaseMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsBaseMatcher" + i,
                        jenkinsBaseMatcher.group(i));
                logger.log(Level.INFO,
                        "group {0}: {1}",
                        new Object[]{i, jenkinsBaseMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * jenkinsCookieMinMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsCookieMinMatcher() {
        logger.info("\n\n\n jenkinsCookieMinMatcher \n");
        Matcher jenkinsCookieMinMatcher
                = testPattern.matcher("<script "
                        + "src=\"/jenkins/static/e04b8d8c/scripts/yui/cookie/cookie-min.js\">");
        if (jenkinsCookieMinMatcher.find()) {
            for (int i = 0; i < jenkinsCookieMinMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsCookieMinMatcher" + i,
                        jenkinsCookieMinMatcher.group(i));
                logger.log(Level.INFO, "group {0}: {1}",
                        new Object[]{i, jenkinsCookieMinMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no jenkinsCookieMinMatcher found");
            assertTrue(false);
        }
    }

    /**
     * jenkinsNewJobMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsNewJobMatcher() {
        logger.info("\n\n\n jenkinsNewJobMatcher \n");
        Matcher jenkinsNewJobMatcher = testPattern.matcher(
                "<a href=\"/jenkins/view/All/newJob\" class=\"task-icon-link\">");
        if (jenkinsNewJobMatcher.find()) {
            for (int i = 0; i < jenkinsNewJobMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsNewJobMatcher " + i,
                        jenkinsNewJobMatcher.group(i));
                logger.log(Level.INFO, "group {0}: {1}",
                        new Object[]{i, jenkinsNewJobMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no jenkinsNewJobMatcher found");
            assertTrue(false);
        }
    }

    /**
     * jenkinsCssMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsCssMatcher() {
        logger.info("\n\n\n jenkinsCssMatcher  \n");
        Matcher jenkinsCssMatcher = testPattern.matcher(
                "<link rel=\"stylesheet\" "
                + "href=\"/jenkins/static/e04b8d8c/css/style.css\" "
                + "type=\"text/css\" />");
        if (jenkinsCssMatcher.find()) {
            for (int i = 0; i < jenkinsCssMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsCssMatcher" + i,
                        jenkinsCssMatcher.group(i));
                logger.log(Level.INFO, "jenkinsCssMatcher{0}: {1}",
                        new Object[]{i, jenkinsCssMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * jenkinsAllNewJobMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsAllNewJobMatcher() {
        logger.info("\n\n\n jenkinsAllNewJobMatcher \n");
        Matcher jenkinsAllNewJobMatcher
                = testPattern.matcher("<a href=\"/jenkins/view/All/newJob\" "
                        + "class=\"task-icon-link\">");
        if (jenkinsAllNewJobMatcher.find()) {
            for (int i = 0; i < jenkinsAllNewJobMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsAllNewJobMatcher" + i,
                        jenkinsAllNewJobMatcher.group(i));
                logger.log(Level.INFO, "jenkinsAllNewJobMatcher{0}: {1}",
                        new Object[]{i, jenkinsAllNewJobMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * jenkinsJobLinkMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsJobLinkMatcher() {
        logger.info("\n\n\n jenkinsJobLinkMatcher\n");
        Matcher jenkinsJobLinkMatcher
                = testPattern.matcher(
                        "<a href=\"job/apacheds-war/lastSuccessfulBuild/\" "
                        + "class=\"model-link inside\">#1</a>");
        if (jenkinsJobLinkMatcher.find()) {
            for (int i = 0; i < jenkinsJobLinkMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsJobLinkMatcher" + i,
                        jenkinsJobLinkMatcher.group(i));
                logger.log(Level.INFO, "jenkinsJobLinkMatcher{0}: {1}",
                        new Object[]{i, jenkinsJobLinkMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * jenkinsJobBuildLinkMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsJobBuildLinkMatcher() {
        logger.info("\n\n\n jenkinsJobBuildLinkMatcher \n");
        Matcher jenkinsJobBuildLinkMatcher
                = testPattern.matcher("<a href=\"job/apacheds-war/lastBuild\" "
                        + "class=\"build-health-link\">");
        if (jenkinsJobBuildLinkMatcher.find()) {
            for (int i = 0; i < jenkinsJobBuildLinkMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsJobBuildLinkMatcher" + i,
                        jenkinsJobBuildLinkMatcher.group(i));
                logger.log(Level.INFO, "jenkinsJobBuildLinkMatcher{0}: {1}",
                        new Object[]{i, jenkinsJobBuildLinkMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * jenkinsJobWarLinkMatcher
     */
    @RunAsClient
    @Test
    public void testJenkinsJobWarLinkMatcher() {
        logger.info("\n\n\n  jenkinsJobWarLinkMatcher \n");
        Matcher jenkinsJobWarLinkMatcher
                = testPattern.matcher("<a href=\"job/apacheds-war/\" "
                        + "class=\"model-link inside\">apacheds<wbr>-war</a>");
        if (jenkinsJobWarLinkMatcher.find()) {
            for (int i = 0; i < jenkinsJobWarLinkMatcher.groupCount(); i++) {
                writtenUrls.put("jenkinsJobWarLinkMatcher" + i,
                        jenkinsJobWarLinkMatcher.group(i));
                logger.log(Level.INFO, "jenkinsJobWarLinkMatcher{0}: {1}",
                        new Object[]{i, jenkinsJobWarLinkMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishJSecurityCheck
     */
    @RunAsClient
    @Test
    public void testGlassfishJSecurityCheck() {
        logger.info("\n\n\n glassfishJSecurityCheck \n");
        Matcher glassfishJSecurityCheckMatcher = testPattern.matcher(
                "<form method=\"POST\" class=\"form\" name=\"loginform\" "
                + "action=\"j_security_check\">");
        if (glassfishJSecurityCheckMatcher.find()) {
            for (int i = 0;
                    i < glassfishJSecurityCheckMatcher.groupCount();
                    i++) {
                writtenUrls.put("glassfishJSecurityCheckMatcher" + i,
                        glassfishJSecurityCheckMatcher.group(i));
                logger.log(Level.INFO,
                        "group{0}: {1}",
                        new Object[]{i,
                            glassfishJSecurityCheckMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }

    }

    /**
     * glassfishDojoMatcher
     *
     * Ignore the test for now. can no longer find this tag on the gf admin page
     */
    @Ignore
    @RunAsClient
    @Test
    public void testGlassfishDojoMatcher() {
        logger.info("\n\n\n glassfishDojoMatcher \n");
        Matcher glassfishDojoMatcher
                = testPattern.matcher("document.write(\"<scr\" + \"ipt "
                        + "type='text/javascript' "
                        + "src='\" + _f4 + \"'></scr\" + \"ipt>\");");
        if (glassfishDojoMatcher.find()) {
            for (int i = 0; i < glassfishDojoMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishDojoMatcher" + i,
                        glassfishDojoMatcher.group(i));
                logger.log(Level.INFO, "glassfishDojoMatcher{0}: {1}",
                        new Object[]{i, glassfishDojoMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no dojoTest match found");
            assertTrue(false);
        }
    }

    /**
     * glassfishDojoBaseMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishDojoBaseMatcher() {
        logger.info("\n\n\n glassfishDojoBaseMatcher \n");
        Matcher glassfishDojoBaseMatcher = testPattern.matcher("<script "
                + "type=\"text/javascript\" "
                + "src=\"/keyhole/gf/theme/META-INF/dojo/dojo.js\"></script>");
        if (glassfishDojoBaseMatcher.find()) {
            for (int i = 0; i < glassfishDojoBaseMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishDojoBaseMatcher" + i,
                        glassfishDojoBaseMatcher.group(i));
                logger.log(Level.INFO, "glassfishDojoBaseMatcher{0}: {1}",
                        new Object[]{i, glassfishDojoBaseMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no glassfishDojoBaseMatcher match found");
            assertTrue(false);
        }
    }

    /**
     * glassfishPngMissingQuoteMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishPngMissingQuoteMatcher() {
        logger.info("\n\n\n glassfishPngMissingQuoteMatcher \n");
        Matcher glassfishPngMissingQuoteMatcher = testPattern.matcher("<div "
                + "style=\"height: 435px;background-image: "
                + "url(/resource/community-theme/images/login-backimage-open.png);\n"
                + "background-repeat:no-repeat;background-position:left top; "
                + "width: 720px; margin: auto;\">");
        if (glassfishPngMissingQuoteMatcher.find()) {
            for (int i = 0;
                    i < glassfishPngMissingQuoteMatcher.groupCount();
                    i++) {
                writtenUrls.put("glassfishPngMissingQuoteMatcher" + i,
                        glassfishPngMissingQuoteMatcher.group(i));
                logger.log(Level.INFO,
                        "glassfishPngMissingQuoteMatcher {0}: {1}",
                        new Object[]{i,
                            glassfishPngMissingQuoteMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no pngMissingQuote match found");
            assertTrue(false);
        }
    }

    /**
     * glassfishImage11Matcher
     */
    @RunAsClient
    @Test
    public void testGlassfishImage11Matcher() {
        logger.info("\n\n\n glassfishImage11Matcher \n");
        Matcher glassfishImage11Matcher = testPattern.matcher(
                "<img id=\"sun_image11\" "
                + "src=\"/resource/community-theme/images/login-product_name_open.png\" "
                + "alt=\"GlassFish Server Open Source Edition\" "
                        + "height=\"42\" width=\"329\" border=\"0\" />");
        if (glassfishImage11Matcher.find()) {
            for (int i = 0; i < glassfishImage11Matcher.groupCount(); i++) {
                writtenUrls.put("glassfishImage11Matcher"
                        + i, glassfishImage11Matcher.group(i));
                logger.log(Level.INFO, "glassfishImage11Matcher{0}: {1}",
                        new Object[]{i, glassfishImage11Matcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no image11 match found");
            assertTrue(false);
        }
    }

    /**
     * glassfishUrlMissingQuoteMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishURLMissingQuoteMatcher() {
        logger.info("\n\n\n glassfishUrlMissingQuoteMatcher \n");
        Matcher glassfishUrlMissingQuoteMatcher = testPattern.matcher(
                "<div class=\"footer\" style=\"background-image: "
                        + "url(/theme/com/sun/webui/jsf/suntheme/images/login/gradlogbot.jpg);"
                        + "background-repeat:repeat-x;background-position:left top; "
                        + "color: #FFFFFF; background-color: #4A5C68\">");
        if (glassfishUrlMissingQuoteMatcher.find()) {
            for (int i = 0;
                    i < glassfishUrlMissingQuoteMatcher.groupCount();
                    i++) {
                writtenUrls.put("glassfishUrlMissingQuoteMatcher" + i,
                        glassfishUrlMissingQuoteMatcher.group(i));
                logger.log(Level.INFO,
                        "glassfishUrlMissingQuoteMatcher{0}: {1}",
                        new Object[]{i,
                            glassfishUrlMissingQuoteMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no urlMissingQuote match found");
            assertTrue(false);
        }
    }

    /**
     * glassfishActionTagMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishActionTagMatcher() {
        logger.info("\n\n\n glassfishActionTagMatcher \n");
        Matcher glassfishActionTagMatcher = testPattern.matcher("<form "
                + "method=\"POST\" class=\"form\" name=\"loginform\" "
                + "action=\"j_security_check\">");
        if (glassfishActionTagMatcher.find()) {
            for (int i = 0; i < glassfishActionTagMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishActionTagMatcher" + i,
                        glassfishActionTagMatcher.group(i));
                logger.log(Level.INFO, "glassfishActionTagMatcher{0}: {1}",
                        new Object[]{i, glassfishActionTagMatcher.group(i)});
            }
            assertTrue(true);
        } else {
            logger.info("no action match found");
            assertTrue(false);
        }
    }

    /**
     * glassfishBaseScriptTagMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishBaseScriptTagMatcher() {
        logger.info("\n\n\n glassfishBaseScriptTagMatcher \n");
        Matcher glassfishBaseScriptTagMatcher
                = testPattern.matcher("<script type=\"text/javascript\" "
                        + "src=\"/theme/META-INF/dojo/dojo.js\"></script>");
        if (glassfishBaseScriptTagMatcher.find()) {
            for (int i = 0; 
                    i < glassfishBaseScriptTagMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishBaseScriptTagMatcher" + i,
                        glassfishBaseScriptTagMatcher.group(i));
                logger.log(Level.INFO, "glassfishBaseScriptTagMatcher{0}: {1}",
                        new Object[]{i, 
                            glassfishBaseScriptTagMatcher.group(i)});
            }
            logger.info("\n\n\n");
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishBaseCssTagMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishBaseCssTagMatcher() {
        logger.info("\n\n\n glassfishBaseCssTagMatcher \n");
        Matcher glassfishBaseCssTagMatcher
                = testPattern.matcher("<link rel=\"stylesheet\" "
                        + "type=\"text/css\" "
                        + "href=\"/theme/com/sun/webui/jsf/suntheme/css/css_master.css\" />");
        if (glassfishBaseCssTagMatcher.find()) {
            for (int i = 0; i < glassfishBaseCssTagMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishBaseCssTagMatcher" + i,
                        glassfishBaseCssTagMatcher.group(i));
                logger.log(Level.INFO, "glassfishBaseCssTagMatcher{0}: {1}",
                        glassfishBaseCssTagMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishImperfectBackgroundImageUrlMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishImperfectBackgroundImageUrlMatcher() {
        logger.info("\n\n\n  glassfishImperfectBackgroundImageUrlMatcher \n");
        Matcher glassfishImperfectBackgroundImageUrlMatcher
                = testPattern.matcher("<div id=\"header\"class=\"LogTopBnd\" "
                        + "style=\"background: "
                        + "url('/theme/com/sun/webui/jsf/suntheme/images/login/gradlogtop.jpg') "
                        + "repeat-x; height: 30px;\"></div>");
        if (glassfishImperfectBackgroundImageUrlMatcher.find()) {
            for (int i = 0;
                    i < glassfishImperfectBackgroundImageUrlMatcher.groupCount();
                    i++) {
                writtenUrls.put("glassfishImperfectBackgroundImageUrlMatcher"
                        + i,
                        glassfishImperfectBackgroundImageUrlMatcher.group(i));
                logger.log(Level.INFO,
                        "glassfishImperfectBackgroundImageUrlMatcher{0}: {1}",
                        glassfishImperfectBackgroundImageUrlMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishBackgroundImageUrlMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishBackgroundImageUrlMatcher() {
        logger.info("\n\n\n glassfishBackgroundImageUrlMatcher \n");
        Matcher glassfishBackgroundImageUrlMatcher
                = testPattern.matcher(
                        "<div style=\"height: 435px;background-image: "
                        + "url(/resource/community-theme/images/login-backimage-open.png);\n"
                        + "background-repeat:no-repeat;background-position:"
                                + "left top; width: 720px; margin: auto;\">");
        if (glassfishBackgroundImageUrlMatcher.find()) {
            for (int i = 0;
                    i < glassfishBackgroundImageUrlMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishBackgroundImageUrlMatcher" + i,
                        glassfishBackgroundImageUrlMatcher.group(i));
                logger.log(Level.INFO,
                        "glassfishBackgroundImageUrlMatcher{0}: {1}",
                        glassfishBackgroundImageUrlMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishFooterBackgroundImageMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishFooterBackgroundImageMatcher() {
        logger.info("\n\n\n glassfishFooterBackgroundImageMatcher \n");
        Matcher glassfishFooterBackgroundImageMatcher
                = testPattern.matcher("<div class=\"footer\"\n"
                        + "style=\"background-image: "
                        + "url(/theme/com/sun/webui/jsf/suntheme/images/login/gradlogbot.jpg);"
                        + "background-repeat:repeat-x;"
                        + "background-position:left top; color: #FFFFFF; "
                        + "background-color: #4A5C68\">");
        if (glassfishFooterBackgroundImageMatcher.find()) {
            for (int i = 0;
                    i < glassfishFooterBackgroundImageMatcher.groupCount();
                    i++) {
                writtenUrls.put("glassfishFooterBackgroundImageMatcher" + i,
                        glassfishFooterBackgroundImageMatcher.group(i));
                logger.log(Level.INFO, "glassfishFooterBackgroundImageMatcher{0}: {1}",
                        glassfishFooterBackgroundImageMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishImageTagMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishImageTagMatcher() {
        logger.info("\n\n\n glassfishImageTagMatcher \n");
        Matcher glassfishImageTagMatcher
                = testPattern.matcher("<img id=\"sun_image11\" "
                        + "src=\"/resource/community-theme/images/login-product_name_open.png\" "
                        + "alt=\"GlassFish Server Open Source Edition\" "
                        + "height=\"42\" width=\"329\" border=\"0\" />");
        if (glassfishImageTagMatcher.find()) {
            for (int i = 0; i < glassfishImageTagMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishImageTagMatcher" + i,
                        glassfishImageTagMatcher.group(i));
                logger.log(Level.INFO, "glassfishImageTagMatcher{0}: {1}",
                        glassfishImageTagMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishFormPostRequestMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishFormPostRequestMatcher() {
        logger.info("\n\n\n glassfishFormPostRequestMatcher \n");
        Matcher glassfishFormPostRequestMatcher
                = testPattern.matcher("<form method=\"POST\" class=\"form\" "
                        + "name=\"loginform\" action=\"j_security_check\">");
        if (glassfishFormPostRequestMatcher.find()) {
            for (int i = 0;
                    i < glassfishFormPostRequestMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishFormPostRequestMatcher{0}: {1}",
                        glassfishFormPostRequestMatcher.group(i));
                logger.log(Level.INFO,
                        "glassfishFormPostRequestMatcher{0}: {1}",
                        glassfishFormPostRequestMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishFooterDivUrlMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishFooterDivUrlMatcher() {
        logger.info("\n\n\n glassfishFooterDivUrlMatcher \n");
        Matcher glassfishFooterDivUrlMatcher
                = testPattern.matcher(" <div class=\"footer\"\n"
                        + "style=\"background-image: "
                        + "url(/theme/com/sun/webui/jsf/suntheme/images/login/gradlogbot.jpg);"
                        + "background-repeat:repeat-x;"
                        + "background-position:left top; "
                        + "color: #FFFFFF; background-color: #4A5C68\">");
        if (glassfishFooterDivUrlMatcher.find()) {
            for (int i = 0; i < glassfishFooterDivUrlMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishFooterDivUrlMatcher" + i,
                        glassfishFooterDivUrlMatcher.group(i));
                logger.log(Level.INFO, "footterDivUrlMatcher{0}: {1}",
                        glassfishFooterDivUrlMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishResourceJSMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishResourceJSMatcher() {
        logger.info("\n\n\n glassfishResourceJSMatcher \n");
        Matcher glassfishResourceJSMatcher
                = testPattern.matcher(
                        "<script src=\"/resource/js/cj.js\"></script>");
        if (glassfishResourceJSMatcher.find()) {
            for (int i = 0; i < glassfishResourceJSMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishResourceJSMatcher" + i,
                        glassfishResourceJSMatcher.group(i));
                logger.log(Level.INFO, "glassfishResourceJSMatcher{0}: {1}",
                        glassfishResourceJSMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishJavaxFacesMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishJavaxFacesMatcher() {
        logger.info("\n\n\n glassfishJavaxFacesMatcher \n");
        Matcher glassfishJavaxFacesMatcher
                = testPattern.matcher("<script type=\"text/javascript\" "
                        + "src=\"/javax.faces.resource/jsf.js.jsf?ln=javax.faces\"></script>");
        if (glassfishJavaxFacesMatcher.find()) {
            for (int i = 0; i < glassfishJavaxFacesMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishJavaxFacesMatcher" + i,
                        glassfishJavaxFacesMatcher.group(i));
                logger.log(Level.INFO, "glassfishJavaxFacesMatcher{0}: {1}",
                        glassfishJavaxFacesMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishYahooResourcesMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishYahooResourcesMatcher() {
        logger.info("\n\n\n yahooResourceMatcher \n");
        Matcher glassfishYahooResourcesMatcher
                = testPattern.matcher(
                        "<script src=\"/resources/yui/yahoo-dom-event.js\"></script>");
        if (glassfishYahooResourcesMatcher.find()) {
            for (int i = 0;
                    i < glassfishYahooResourcesMatcher.groupCount();
                    i++) {
                writtenUrls.put("yahooResourceMatcher" + i,
                        glassfishYahooResourcesMatcher.group(i));
                logger.log(Level.INFO, "yahooResourceMatcher{0}: {1}",
                        glassfishYahooResourcesMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishSunScriptMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishSunScriptMatcher() {
        logger.info("\n\n\n glassfishSunScriptMatcher \n");
        Matcher glassfishSunScriptMatcher = testPattern.matcher(
                "<script id=\"sun_script56\" type=\"text/javascript\" "
                + "src=\"/resource/common/js/adminjsf.js\"></script>");
        if (glassfishSunScriptMatcher.find()) {
            for (int i = 0; i < glassfishSunScriptMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishSunScriptMatcher" + i,
                        glassfishSunScriptMatcher.group(i));
                logger.log(Level.INFO, "sunscriptMatcher{0}: {1}",
                        glassfishSunScriptMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishIconLinkMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishIconLinkMatcher() {
        logger.info("\n\n\n glassfishIconLinkMatcher \n");
        Matcher glassfishIconLinkMatcher
                = testPattern.matcher("<link rel=\"icon\" "
                        + "href=\"/resource/community-theme/images/favicon.ico\" "
                        + "type=\"image/x-icon\" />");
        if (glassfishIconLinkMatcher.find()) {
            for (int i = 0; i < glassfishIconLinkMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishIconLinkMatcher" + i,
                        glassfishIconLinkMatcher.group(i));
                logger.log(Level.INFO, "glassfishIconLinkMatcher{0}: {1}",
                        glassfishIconLinkMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    /**
     * glassfishImageMastheadMatcher
     */
    @RunAsClient
    @Test
    public void testGlassfishImageMastheadMatcher() {
        logger.info("\n\n\n glassfishImageMastheadMatcher \n");
        Matcher glassfishImageMastheadMatcher
                = testPattern.matcher("<img id=\"Masthead:skipUtility_icon\" "
                        + "src=\"/theme/com/sun/webui/jsf/suntheme/images/other/dot.gif\" "
                        + "alt=\"\" title=\"Jump to Status Area of Masthead\" "
                        + "height=\"1\" width=\"1\" border=\"0\" />");
        if (glassfishImageMastheadMatcher.find()) {
            for (int i = 0; i < glassfishImageMastheadMatcher.groupCount(); i++) {
                writtenUrls.put("glassfishImageMastheadMatcher{0}: {1}" + 1,
                        glassfishImageMastheadMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    @RunAsClient
    @Test
    public void testDoubleBackSlashMatcher() {
        logger.info("\n\n doubleBackSlashMatcher \n");
        Matcher doubleBackSlashMatcher
                = testPattern.matcher(
                        "<script src=\"//typeface.nytimes.com/zam5nzz.js\"></script>");
        if (doubleBackSlashMatcher.find()) {
            for (int i = 0; i < doubleBackSlashMatcher.groupCount(); i++) {
                writtenUrls.put("doubleBackSlashMatcher{0}: {1}" + 1,
                        doubleBackSlashMatcher.group(i));
            }
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }
}

//    Pattern basePattern = Pattern.compile(baseRegex, Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
//    //Pattern weirdPattern = Pattern.compile("\\b(href=|src=|action=|url)([\"\'])?(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
//    Pattern weirdPattern = Pattern.compile("\\b(href=|src=|action=|url[(])([^\"\'(]*)", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
//    //Pattern actionPattern = Pattern.compile("\\b(href=|src=|action=|url[(])([\"\'])?(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);         
//    Pattern workingUrlMissingQuotePattern = Pattern.compile("\\b(href=|src=|action=|url[(])(([\"\']?)(([^/]+://)([^/<>]+))?([^\"\'>\\)]*)([\"\']?))", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
//the following replicates internal keyhole code
//    @Ignore
//    @RunAsClient
//    @Test
//    public void testGfRegex() throws Exception {
////        Pattern gfPattern = Pattern.compile("\\b(href=|src=|action=|url=)([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
//        Pattern gfPattern = Pattern.compile("\\b(href=|src=|action=|url\\()([\"\'])(([^/]+://)([^/<>]+))?([^\"\'>]*)[\"\']");
//        Matcher dojoLinkMatcher = gfPattern.matcher("document.write(\"<scr\" + \"ipt type='text/javascript' src='\" + _f4 + \"'></scr\" + \"ipt>\");");
//        StringBuffer page = new StringBuffer();
//        Server server = new BaseServer();
//
//        if (dojoLinkMatcher.find()) {
//            for (int i = 0; i < dojoLinkMatcher.groupCount(); i++) {
//                System.out.println("&&&&&&&&&&& group" + i + ": " + dojoLinkMatcher.group(i));
//            }
//        }
//
//        String link = dojoLinkMatcher.group(6).replaceAll("\\$", "\\\\\\$");
//        if (link.length() == 0) {
//            link = "/";
//        }
//
//        String rewritten = null;
//        if (dojoLinkMatcher.group(4) != null) {
//            rewritten = handleExternalLink(server, dojoLinkMatcher, link);
//        } else if (link.startsWith("/")) {
//            rewritten = handleLocalLink(server, dojoLinkMatcher, link);
//        }
//
//        if (rewritten != null) {
//            logger.finest("Found link " + link + " >> " + rewritten);
//            dojoLinkMatcher.appendReplacement(page, rewritten);
//        }
//
//        dojoLinkMatcher.appendTail(page);
//        logger.info(page.toString());
//
//    }
//
//    private String handleExternalLink(Server server, Matcher matcher, String link) {
//
//        String location = matcher.group(5) + link;
////        Server matchingServer = serverChain.getServerMapped(location);
//        Server matchingServer = server;
//        String ownHostName = "myHost";
//        String contextPath = baseUrl.toString();
//
//        if (matchingServer != null) {
//            link = link.substring(matchingServer.getPath().length());
//            link = matchingServer.getRule().revert(link);
//            String type = matcher.group(1);
//            String leadingSeparator = matcher.group(2);
//            String trailingSeperator = null;
//            String protocol = matcher.group(4);
//            logger.info("externalLink leadingSeparator: " + leadingSeparator);
//            logger.info("externalLink trailingSeparator: " + trailingSeperator);
//            return type + leadingSeparator + protocol + ownHostName + contextPath + link + leadingSeparator;
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     *
//     * @param server The current server we are using for this page
//     * @param matcher The matcher used for this link
//     * @param link The original link
//     * @return The rewritten link
//     */
//    private String handleLocalLink(Server server, Matcher matcher, String link) {
//        String serverDir = server.getPath();
//        String contextPath = baseUrl.toString();
//
//        String type = matcher.group(1);
//        String leadingSeparator = matcher.group(2);
//        String trailingSeparator = leadingSeparator;
//
//        Pattern localLinkPattern = Pattern.compile("[\\D]*'\"");
//        logger.info("matcher.group(0): " + matcher.group(0));
//        Matcher localLinkMatcher = localLinkPattern.matcher(matcher.group(0));
//        if (localLinkMatcher.find()) {
//            trailingSeparator = "\"";
//        }
//
//        if (serverDir.equals("") || link.startsWith(serverDir + "/")) {
////            link = server.getRule().revert(link.substring(serverDir.length()));
//
//            logger.info("localLink leadingSeparator: " + leadingSeparator);
//            logger.info("localLink trailingSeparator: " + trailingSeparator);
//            String localLink = type + leadingSeparator + contextPath + link + trailingSeparator;
//            logger.info("local Link result: " + localLink);
//            return localLink;
//        } else {
//            return null;
//        }
//    }
//
//    public void beginNormalRequest(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/main.jsp", null, null);
//    }
//    
//    public void testNormalRequest() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endNormalRequest(WebResponse theResponse) {
//        assertEquals("The response code should be 200", 200, theResponse.getStatusCode());
//        assertEquals("Checking for correct page", "/GET/main.jsp", theResponse.getText());
//    }
//    
//    public void begin404(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/nonexistant.jsp", null, null);
//    }
//    
//    public void test404() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void end404(WebResponse theResponse) {
//        assertEquals("The response code should be 404", 404, theResponse.getStatusCode());
//    }
//    
//    public void beginNonExistentServer(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/testNonExistentServer/", null, null);
//    }
//    
//    public void testNonExistentServer() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endNonExistentServer(WebResponse theResponse) {
//        assertEquals("The response code should be 504", 504, theResponse.getStatusCode());
//    }
//    
//    public void beginConditional(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/image.gif", null, null);
//        theRequest.addHeader("If-Unmodified-Since", "Wed, 20 Jul 2000 15:00:00 GMT");
//    }
//    
//    public void testConditional() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endConditional(WebResponse theResponse) {
//        assertEquals("The response code should be 412", 412, theResponse.getStatusCode());
//    }
//    
//    public void beginUnhandledMethod(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/", null, null);
//    }
//    
//    public void testUnhandledMethod() throws ServletException, IOException {
//        
//        MethodWrappingRequest req = new MethodWrappingRequest(request, "JDFJDSJSN");
//        proxyFilter.doFilter(req, response, filterChain);
//
//    }
//
//    public void endUnhandledMethod(WebResponse theResponse) {
//        assertEquals("Checking that we got a 405 response", 405, theResponse.getStatusCode());
//        assertEquals("Correct options not returned",
//                "OPTIONS,GET,HEAD,POST,PUT,DELETE,TRACE", theResponse.getConnection()
//                        .getHeaderField("Allow"));
//    }
//    
//    public void begin405(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/405.jsp", null, null);
//    }
//    
//    public void test405() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void end405(WebResponse theResponse) {
//        assertEquals("The response code should be 405", 405, theResponse.getStatusCode());
//        String allow = theResponse.getConnection().getHeaderField("Allow");
//
//        assertTrue("Should include OPTIONS", allow.contains("OPTIONS"));
//        assertTrue("Should include GET", allow.contains("GET"));
//        assertFalse("Shouldn't include MYOWNHEADER", allow.contains("MYOWNHEADER"));
//        assertFalse("Shouldn't include PROPFIND", allow.contains("PROPFIND"));
//    }
//    
//    public void beginVia(WebRequest theRequest) {
//        theRequest.setURL("localhost:8080", "/test", "/GET/main.jsp", null, null);
//    }
//    
//    public void testVia() throws IOException, ServletException {
//        proxyFilter.doFilter(request, response, filterChain);
//    }
//    
//    public void endVia(WebResponse theResponse) {
//        assertEquals("The response code should be 200", 200, theResponse.getStatusCode());
//        String via = theResponse.getConnection().getHeaderField("Via").trim();
//
//        try {
//            String serverHostName = InetAddress.getLocalHost().getHostName();  
//            String expected = "HTTP/1.1 " + serverHostName;
//            assertEquals("Checking that the via header is included", expected, via);
//        } catch (UnknownHostException e) {
//            fail("Couldn't get the hostname needed for header Via");
//        }
//    }

