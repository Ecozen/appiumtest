package test.appium.topdeep_appium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class WorktileTest {

	  private AndroidDriver<?> driver;
	
	  @Before
	  public void setup() throws Exception {
	    File classpathRoot = new File(System.getProperty("user.dir"));
	    File appDir = new File(classpathRoot,"apps");
	    File app = new File(appDir, "worktile_3.10.2.apk");
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
	  public void currentActivityTest() {
	    String activity = driver.currentActivity();
	    assertEquals(".worktile", activity);
	  }

	
	  public void isAppInstalledTest() {
	    assertTrue(driver.isAppInstalled("com.worktile"));
	  }
	  
	 
	  public void LoginTest(){
		  driver.startActivity("com.worktile","com.worktile.ui.external.ExternalActivity", null, null);  
		  assertEquals("com.worktile.ui.external.ExternalActivi", driver.currentActivity());
		  
		  WebElement element = driver.findElementById("btn_login");
		  //AndroidElement element = (AndroidElement) driver.findElement(MobileBy.AndroidUIAutomator(""));
		  element.click();
		  
		  element = driver.findElementById("et_username");
		  element.sendKeys("176595210@qq.com");
		  
		  element = driver.findElementById("et_password");
		  element.sendKeys("zwx1988");
		  
		  element = driver.findElementById("btn_login");
		  element.click();
		  
		  assertEquals("com.worktile.ui.main.MainActivity",driver.currentActivity());
	  }
}
