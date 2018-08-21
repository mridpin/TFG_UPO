/**
 * Makes input fields in profile page editable
 * 
 * @returns
 */
function makeEditable() {
	$(".editable").removeAttr("disabled");
	$(".editable").removeAttr("readonly");
}

/**
 * Checks if both passwords match to prevent typos
 * 
 * @returns
 */
function matchPasswords() {
	var pass = $("#inputPassword").val();
	var checkPass = $("#checkPassword").val();
	if (pass.length > 0 && checkPass.length > 0 && pass === checkPass) {
		return true;
	} else {
		alert("Passwords don't match!");
		return false;
	}
}

/**
 * On the game page, calculates the total score of each war
 * 
 * @returns
 */
function calculateRollTotals() {
	var tables = $(".rolls_per_turn");
	for (var t = 0; t<tables.length; t++) {
		var attackerScores = $(tables[t]).find("tr.attacker_score_cell");
		var attackerTotal = 0.0;
		for (var r = 0; r<attackerScores.length; r++) {
			attackerTotal += $(attackerScores[r]).text();
		}
		var defenderScores = $(tables[t]).find("tr.defender_score_cell");
		var defenderTotal = 0.0;
		for (var r = 0; r<defenderScores.length; r++) {
			defenderTotal += $(defenderScores[r]).text();
		}
		$(tables[t]).find("th.attacker_score").text(attackerTotal);
		$(tables[t]).find("th.defender_score").text(defenderTotal);
	}
}

/**
 * Extends the String prototype to add a replaceAll function
 */
String.prototype.replaceAll = function(search, replace) {
    if (replace === undefined) {
        return this.toString();
    }
    return this.split(search).join(replace);
}

$.validator.addMethod('alphanumeric', function (value) { 
	var regex = RegExp(/^[a-z0-9]+$/i);
	return regex.test(value);
});

