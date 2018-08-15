package es.upo.tfg.rol.controller.controllers.validators;

import java.util.List;

/**
 * Holds error messages to valdiate Game and Country creation
 * @author ridao
 *
 */
public class GameValidator implements Validator {

	private String gameNameError;
	private String scenarioError;
	private String addedGMError;
	private String duplicatePlayerError;
	private String duplicateCountryError;
	private String playerCountError;
	private List<String> countryFileError;
	private String playerDoesntExistError;

	public GameValidator() {
		// TODO Auto-generated constructor stub
	}

	public String getGameNameError() {
		return gameNameError;
	}

	public void setGameNameError(String gameNameError) {
		this.gameNameError = gameNameError;
	}

	public String getScenarioError() {
		return scenarioError;
	}

	public void setScenarioError(String scenarioError) {
		this.scenarioError = scenarioError;
	}

	public String getAddedGMError() {
		return addedGMError;
	}

	public void setAddedGMError(String addedGMError) {
		this.addedGMError = addedGMError;
	}

	public String getDuplicatePlayerError() {
		return duplicatePlayerError;
	}

	public void setDuplicatePlayerError(String duplicatePlayerError) {
		this.duplicatePlayerError = duplicatePlayerError;
	}

	public String getPlayerCountError() {
		return playerCountError;
	}

	public void setPlayerCountError(String playerCountError) {
		this.playerCountError = playerCountError;
	}

	public String getDuplicateCountryError() {
		return duplicateCountryError;
	}

	public void setDuplicateCountryError(String duplicateCountryError) {
		this.duplicateCountryError = duplicateCountryError;
	}

	public List<String> getCountryFileError() {
		return countryFileError;
	}

	public void setCountryFileError(List<String> countryFileError) {
		this.countryFileError = countryFileError;
	}

	public String getPlayerDoesntExistError() {
		return playerDoesntExistError;
	}

	public void setPlayerDoesntExistError(String playerDoesntExistError) {
		this.playerDoesntExistError = playerDoesntExistError;
	}

	@Override
	public boolean validate() {
		boolean res = true;
		res = res && getGameNameError() == null;
		res = res && getAddedGMError() == null;
		res = res && getDuplicatePlayerError() == null;
		res = res && getScenarioError() == null;
		res = res && getPlayerCountError() == null;
		res = res && getDuplicateCountryError() == null;
		res = res && getCountryFileError() == null;
		res = res && getPlayerDoesntExistError() == null;
		return res;
	}

}
