package es.upo.tfg.rol;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Random;

import javax.validation.constraints.NotNull;

import org.junit.Before;

public class TestUtils {

	public final static String STATIC_FILE_PATH = "static_files";
	public static final String TEST_COUNTRY_FILE_PATH = TestUtils.STATIC_FILE_PATH
			+ File.separator + "Alemania.csv";
	public static final String TEST_DEMAPCOUNTRY_FILE_PATH = TestUtils.STATIC_FILE_PATH
			+ File.separator + "AlemaniaDemap.csv";
	public static final String TEST_COUNTRYERROR_FILE_PATH = TestUtils.STATIC_FILE_PATH
			+ File.separator + "AlemaniaErrors.csv";
	public static final String TEST_SCENARIO_FILE_PATH = TestUtils.STATIC_FILE_PATH
			+ File.separator + "TestScenario.csv";
	public static final String TEST_SCENARIOERRORS_FILE_PATH = TestUtils.STATIC_FILE_PATH
			+ File.separator + "TestScenarioErrors.csv";

	public static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String createRandomString() {
		// Random nickname to prevent uniqueness constrant
		char[] string = new char[12];
		Random r = new Random();
		for (int i = 0; i < 12; i++) {
			int index = r.nextInt(ALPHANUMERIC.length() - 1);
			string[i] = ALPHANUMERIC.charAt(index);
		}
		return new String(string);
	}

}
