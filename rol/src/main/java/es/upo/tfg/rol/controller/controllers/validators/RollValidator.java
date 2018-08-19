package es.upo.tfg.rol.controller.controllers.validators;

/**
 * Validates the form in page war.hmtl by storing messages
 * 
 * @author ridao
 */
public class RollValidator implements Validator {

	private String warNameError;
	private String attackerNameError;
	private String defenderNameError;
	private String attackerZeroCountError;
	private String defenderZeroCountError;
	private String countryInv;
	private String countryNotRecognized;
	private String turnError;
	private String genericRollError;

	public RollValidator() {
		// TODO Auto-generated constructor stub
	}

	public String getWarNameError() {
		return warNameError;
	}

	public void setWarNameError(String warNameError) {
		this.warNameError = warNameError;
	}

	public String getAttackerNameError() {
		return attackerNameError;
	}

	public void setAttackerNameError(String attackerNameError) {
		this.attackerNameError = attackerNameError;
	}

	public String getDefenderNameError() {
		return defenderNameError;
	}

	public void setDefenderNameError(String defenderNameError) {
		this.defenderNameError = defenderNameError;
	}

	public String getAttackerZeroCountError() {
		return attackerZeroCountError;
	}

	public void setAttackerZeroCountError(String attackerZeroCountError) {
		this.attackerZeroCountError = attackerZeroCountError;
	}

	public String getDefenderZeroCountError() {
		return defenderZeroCountError;
	}

	public void setDefenderZeroCountError(String defenderZeroCountError) {
		this.defenderZeroCountError = defenderZeroCountError;
	}	

	public String getCountryInv() {
		return countryInv;
	}

	public void setCountryInv(String countryInv) {
		this.countryInv = countryInv;
	}	

	public String getGenericRollError() {
		return genericRollError;
	}

	public void setGenericRollError(String genericRollError) {
		this.genericRollError = genericRollError;
	}
		
	public String getTurnError() {
		return turnError;
	}

	public void setTurnError(String turnError) {
		this.turnError = turnError;
	}

	public String getCountryNotRecognized() {
		return countryNotRecognized;
	}

	public void setCountryNotRecognized(String countryNotRecognized) {
		this.countryNotRecognized = countryNotRecognized;
	}

	@Override
	public boolean validate() {
		boolean res = true;
		res = res && getWarNameError() == null;
		res = res && getAttackerNameError() == null;
		res = res && getDefenderNameError() == null;
		res = res && getAttackerZeroCountError() == null;
		res = res && getDefenderZeroCountError() == null;
		res = res && getCountryInv() == null;
		res = res && getGenericRollError() == null;
		res = res && getCountryNotRecognized() == null;
		res = res && getTurnError() == null;
		return res;
	}


}
