$(document).ready(function() {

	$('#sidebarCollapse').on('click', function() {
		$('#genotypeSearchSidebar').toggleClass('active');
		$(this).toggleClass('active');

		if ($(this).find('i').attr('class') == 'fa fa-chevron-left') {
			$(this).find('i').removeClass('fa fa-chevron-left').addClass('fa fa-chevron-right');
			$("#sidebarCollapse1").show();
		} else {
			$(this).find('i').removeClass('fa fa-chevron-right').addClass('fa fa-chevron-left');
			$("#sidebarCollapse1").hide();
		}

	});
	
	
	
	$('#sidebarCollapse3').on('click', function() {
		alert('test3');
		
	});

	$('#step1Div').hover(function() {
		$("#varietyInputBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#varietyInputBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#step2Div').hover(function() {
		$("#regionInputBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#regionInputBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#step3Div').hover(function() {
		$("#optionInputBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#optionInputBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#finalStepDiv').hover(function() {
		$("#buttonBox").css({
			'border-color': 'red',
			'border-width': '2px',
			'border-style': 'solid',
			'margin': '1px'
		})
	}, function() {
		$("#buttonBox").css({
			'border-color': '#ffffff00',
			'border-width': '0px',
			'border-style': 'none'
		});

	});

	$('#chrInput').hover(function() {
		$("#startInput").css({
			'border-color': 'green',
			'border-width': '3px',
			'border-style': 'solid',
			'margin': '1px'
		});
		$("#endInput").css({
			'border-color': 'green',
			'border-width': '3px',
			'border-style': 'solid',
			'margin': '1px'
		});
		$("#chrInput").css({
			'border-color': 'green',
			'border-width': '3px',
			'border-style': 'solid',
			'margin': '1px'
		});

	}, function() {
		$("#startInput").css({
			'border': '#ced4da',
			'border-width': '1px',
			'border-style': 'solid'
		});
		$("#endInput").css({
			'border-color': '#ced4da',
			'border-width': '1px',
			'border-style': 'solid'
		});
		$("#chrInput").css({
			'border-color': '#ced4da',
			'border-width': '1px',
			'border-style': 'solid'
		});
	});


	$('#sidebarCollapse1').on('click', function() {
		$('#genotypeSearchSidebar').toggleClass('active');
		$(this).toggleClass('active');

		if ($('#sidebarCollapse').find('i').attr('class') == 'fa fa-chevron-left') {
			$('#sidebarCollapse').find('i').removeClass('fa fa-chevron-left').addClass('fa fa-chevron-right');
		} else {
			$('#sidebarCollapse').find('i').removeClass('fa fa-chevron-right').addClass('fa fa-chevron-left');
		}
		$("#sidebarCollapse1").hide();





	});

	$("#bodyContent").scroll(function() {
		alert("test");
	});


});

$(window).on("load", function() {
	window.open("http://localhost:8080/SNP-seekV3_clean/ads.html","ads", "width=650", "height=200");
	$("#sidebarCollapse1").hide();

});



$(document).on('click', '.sidebarCollapse3', function(){ 
     alert('testCollapse');
});

function myFunction() {
 	var element = document.getElementById("genotypeSearchSidebar");
 	element.classList.toggle("active");
}