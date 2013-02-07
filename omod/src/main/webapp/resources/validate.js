$(document).ready(function() {

	$("#nid").keypress(function(event) {

		if (event.keyCode == 46 || event.keyCode == 8) {
			// let it happen, don't do anything

		} else {
			// Ensure that it is a number and stop the keypress
			if (event.keyCode < 48 || event.keyCode > 57) {
				event.preventDefault();
				alert("The NID Field only takes numeric values");
			}
		}
	});

	$("form").submit(function() {
		// var name = $('#rwandanName').val();
		var error = 0;
		var isComplete = true;
		if ($('#locationId').val() == 0) {
			$('#locationerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#locationerror').addClass('obscure');
			isComplete = true;
			error = 0;
		}
		if ($('#rwandanName').val() == "") {
			$('#rwerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#rwerror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#angloName').val() == "") {
			$('#angerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#angerror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}


		if ($('#nid').val() == "") {
			$('#niderror').removeClass('obscure');
			$('#nidrange').addClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			var idLength = $('#nid').val().length;
			if (idLength != 15) {
				$('#nidrange').removeClass('obscure');
				$('#niderror').addClass('obscure');
				isComplete = false;
			} else {
				$('#nidrange').addClass('obscure');
				$('#niderror').addClass('obscure');
				if (error != 1) {
					isComplete = true;
				}
			}

		}
		if ($('#province').val() == 0) {
			$('#proverror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#proverror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#district').val() == "") {
			$('#disterror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#disterror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#sector').val() == "") {
			$('#sectorerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#sectorerror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#cell').val() == "") {
			$('#cellerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#cellerror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#village').val() == "") {
			$('#villerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#villerror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#insurance').val() == 0) {
			$('#inserror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#inserror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		if ($('#insuranceNumber').val() == "") {
			$('#insNumerror').removeClass('obscure');
			isComplete = false;
			error = 1;
		} else {
			$('#insNumerror').addClass('obscure');
			if (error != 1) {
				isComplete = true;
			}
		}
		error=0;
		if (isComplete) {
			$('#errormessage').addClass('obscure');
			return true;
		} else {
			$('#errormessage').removeClass('obscure');
			return false;
		}

	});
});