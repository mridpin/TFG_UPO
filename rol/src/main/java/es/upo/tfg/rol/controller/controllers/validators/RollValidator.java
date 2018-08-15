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
	private String attackerOverCountError;
	private String defenderOverCountError;
	private String attackerZeroCountError;
	private String defenderZeroCountError;

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

	public String getAttackerOverCountError() {
		return attackerOverCountError;
	}

	public void setAttackerOverCountError(String attackerOverCountError) {
		this.attackerOverCountError = attackerOverCountError;
	}

	public String getDefenderOverCountError() {
		return defenderOverCountError;
	}

	public void setDefenderOverCountError(String defenderOverCountError) {
		this.defenderOverCountError = defenderOverCountError;
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

	@Override
	public boolean validate() {
		boolean res = true;
		res = res && getWarNameError() == null;
		res = res && getAttackerNameError() == null;
		res = res && getDefenderNameError() == null;
		res = res && getAttackerOverCountError() == null;
		res = res && getAttackerZeroCountError() == null;
		res = res && getDefenderZeroCountError() == null;
		res = res && getDefenderOverCountError() == null;
		return res;
	}

}
