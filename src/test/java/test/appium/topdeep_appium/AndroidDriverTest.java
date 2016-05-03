/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.appium.topdeep_appium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumSetting;
import io.appium.java_client.NetworkConnectionSetting;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.android.AndroidKeyMetastate;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;

/**
 * Test Mobile Driver features
 */
public class AndroidDriverTest {

  private AndroidDriver<?> driver;
  private static AppiumDriverLocalService service;

//  @BeforeClass
//  public static void beforeClass() throws Exception{
//    service = AppiumDriverLocalService.buildDefaultService();
//    service.start();
//  }

  @Before
  public void setup() throws Exception {
//    if (service == null || !service.isRunning())
//      throw new RuntimeException("An appium server node is not started!");
    File classpathRoot = new File(System.getProperty("user.dir"));
    File appDir = new File(classpathRoot,"apps");
    File app = new File(appDir, "ApiDemos-debug.apk");
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
    capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
    capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
    driver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }

  private String currentActivity() {
    String currentActivity = null;
    while (currentActivity == null) {
      currentActivity = driver.currentActivity();
      Thread.yield();
    }
    return currentActivity;
  }

  @Test
  public void getDeviceTimeTest() {
    String time = driver.getDeviceTime();
    assertTrue(time.length() == 28);
  }
  
  @Test
  public void getStringsTest() {
    Map<String, String> strings = driver.getAppStringMap();
    assertTrue(strings.size() > 100);
  }

  @Test
  public void getStringsWithLanguageTest() {
    Map<String, String> strings = driver.getAppStringMap("en");
    assertTrue(strings.size() > 100);
  }

  @Test
  public void pressKeyCodeTest() {
    driver.pressKeyCode(AndroidKeyCode.HOME);
  }

  @Test
  public void pressKeyCodeWithMetastateTest() {
    driver.pressKeyCode(AndroidKeyCode.SPACE, AndroidKeyMetastate.META_SHIFT_ON);
  }

  @Test
  public void longPressKeyCodeTest() {
    driver.longPressKeyCode(AndroidKeyCode.HOME);
  }

  @Test
  public void longPressKeyCodeWithMetastateTest() {
    driver.longPressKeyCode(AndroidKeyCode.HOME, AndroidKeyMetastate.META_SHIFT_ON);
  }

  @Test
  public void currentActivityTest() {
    String activity = driver.currentActivity();
    assertEquals(".ApiDemos", activity);
  }

  @Test
  public void isAppInstalledTest() {
    assertTrue(driver.isAppInstalled("com.example.android.apis"));
  }

  @Test
  public void isAppNotInstalledTest() {
    assertFalse(driver.isAppInstalled("foo"));
  }

  @Test
  public void closeAppTest() throws InterruptedException {
    driver.closeApp();
    driver.launchApp();
    assertEquals(".ApiDemos", driver.currentActivity());
  }

  @Test
  public void pushFileTest() {
    byte[] data = Base64.encodeBase64("The eventual code is no more than the deposit of your understanding. ~E. W. Dijkstra".getBytes());
    driver.pushFile("/data/local/tmp/remote.txt", data);
    byte[] returnData = driver.pullFile("/data/local/tmp/remote.txt");
    String returnDataDecoded = new String(Base64.decodeBase64(returnData));
    assertEquals("The eventual code is no more than the deposit of your understanding. ~E. W. Dijkstra", returnDataDecoded);
  }

  @Test
  public void networkConnectionTest() {
    NetworkConnectionSetting networkConnection = new NetworkConnectionSetting(false, true, true);

    networkConnection.setData(false);
    networkConnection.setWifi(false);


    driver.setNetworkConnection(networkConnection);
    networkConnection = driver.getNetworkConnection();

    assertEquals(new NetworkConnectionSetting(false, false, false), networkConnection);

  }

  @Test
  public void ignoreUnimportantViews() {
    driver.ignoreUnimportantViews(true);
    boolean ignoreViews = driver.getSettings().get(AppiumSetting.IGNORE_UNIMPORTANT_VIEWS.toString()).getAsBoolean();
    assertTrue(ignoreViews);
    driver.ignoreUnimportantViews(false);
    ignoreViews = driver.getSettings().get(AppiumSetting.IGNORE_UNIMPORTANT_VIEWS.toString()).getAsBoolean();
    assertFalse(ignoreViews);
  }

  @Test
  public void startActivityInThisAppTest() {
    driver.startActivity("io.appium.android.apis", ".os.MorseCode", null, null, false);
    assertTrue(currentActivity().endsWith(".os.MorseCode"));
    driver.findElementById("text").sendKeys("Text must be here!");
    driver.startActivity("io.appium.android.apis", ".accessibility.AccessibilityNodeProviderActivity", null, null, false);
    assertTrue(currentActivity().endsWith(".accessibility.AccessibilityNodeProviderActivity"));
    driver.pressKeyCode(AndroidKeyCode.BACK);
    assertTrue(currentActivity().endsWith(".os.MorseCode"));
    assertEquals("Text must be here!", driver.findElementById("text").getText());
  }

  @Test
  public void stopAndStartActivityInThisAppTest() {
    driver.startActivity("io.appium.android.apis", ".os.MorseCode", null, null);
    assertTrue(currentActivity().endsWith(".os.MorseCode"));
    driver.startActivity("io.appium.android.apis", ".accessibility.AccessibilityNodeProviderActivity", null, null);
    assertTrue(currentActivity().endsWith(".accessibility.AccessibilityNodeProviderActivity"));
    driver.pressKeyCode(AndroidKeyCode.BACK);
    assertFalse(currentActivity().endsWith(".os.MorseCode"));
  }

  @Test
  public void startActivityInAnotherAppTest() {
    driver.startActivity("com.android.contacts", ".ContactsListActivity", null, null);
    String activity = driver.currentActivity();
    assertTrue(activity.contains("Contact"));
  }

  //TODO hideKeyboard() test

  @Test
  public void scrollToTest() {
    driver.scrollTo("View");
    WebElement views = driver.findElementByAccessibilityId("Views");
    assertNotNull(views);
  }

  @Test
  public void scrollToExactTest() {
    driver.scrollToExact("Views");
    WebElement views = driver.findElementByAccessibilityId("Views");
    assertNotNull(views);
  }

  @Test
  public void toggleLocationServicesTest() {
    driver.toggleLocationServices();
  }

  @Test
  public void geolocationTest() {
    Location location = new Location(45, 45, 100);
    driver.setLocation(location);
  }

  @Test
  public void orientationTest() {
    assertEquals(ScreenOrientation.PORTRAIT, driver.getOrientation());
    driver.rotate(ScreenOrientation.LANDSCAPE);
    assertEquals(ScreenOrientation.LANDSCAPE, driver.getOrientation());
    driver.rotate(ScreenOrientation.PORTRAIT);
  }

  @Test
  public void lockTest() {
    driver.lockDevice();
    assertEquals(true, driver.isLocked());
    driver.unlockDevice();
    assertEquals(false, driver.isLocked());
  }

  @Test
  public void runAppInBackgroundTest() {
    long time = System.currentTimeMillis();
    driver.runAppInBackground(4);
    long timeAfter = System.currentTimeMillis();
    assert(timeAfter - time > 3000);
  }

  @Test
  public void pullFileTest() {
    byte[] data = driver.pullFile("data/system/registered_services/android.content.SyncAdapter.xml");
    assert(data.length > 0);
  }

  @Test
  public void resetTest() {
    driver.resetApp();
  }

  @AfterClass
  public static void afterClass(){
    if (service != null)
       service.stop();
  }
}
