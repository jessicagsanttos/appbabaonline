$(document).ready(function() {

	$('#password').keyup(function() {
		$('#result').html(checkStrength($('#password').val()))
	})


	function checkStrength(password) {
		var strength = 0;

		if (password.length < 6) {
			$('#result').removeClass()
			$('#result').addClass('short')
			return 'Too short'
		}

		if (password.length > 7) strength += 1
		if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/)) strength += 1
		if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)) strength += 1
		if (password.match(/([!,%,&,@,#,$,^,*,?,_,~])/)) strength += 1

		if (password.match(/(.*[!,%,&,@,#,$,^,*,?,_,~].*[!,",%,&,@,#,$,^,*,?,_,~])/)) strength += 1

		if (strength < 2) {
			$('#result').removeClass()
			$('#result').addClass('weak')
			return 'Weak'
		}

		else if (strength == 2) {
			$('#result').removeClass()
			$('#result').addClass('good')
			return 'Good'
		} else {
			$('#result').removeClass()
			$('#result').addClass('strong')
			return 'Strong'

		}
	}
});
