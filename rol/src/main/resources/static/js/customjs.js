/**
 * Makes input fields in profile page editable
 * @returns
 */
function makeEditable() {
	$(".editable").removeAttr("disabled");
	$(".editable").removeAttr("readonly");
}

/**
 * Checks if both passwords match to prevent typos
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