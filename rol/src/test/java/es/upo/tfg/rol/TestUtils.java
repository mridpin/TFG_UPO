package es.upo.tfg.rol;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Random;

import javax.validation.constraints.NotNull;

import org.junit.Before;

public class TestUtils {


	public final static String STATIC_FILE_PATH = "static_files";
	public static final String TEST_COUNTRY_FILE_PATH = TestUtils.STATIC_FILE_PATH + File.separator + "Alemania.csv";
	public static final String TEST_COUNTRYERROR_FILE_PATH = TestUtils.STATIC_FILE_PATH + File.separator + "AlemaniaErrors.csv";
	public static final String TEST_SCENARIO_FILE_PATH = TestUtils.STATIC_FILE_PATH + File.separator + "TestScenario.csv";

	public static String createRandomString () {
		// Random nickname to prevent uniqueness constrant
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		return generatedString;
	}

}
