function initFakeButtons(){
	jQuery('.fakeButton').hover(
		function(event){
			jQuery(this).addClass('ui-state-hover');
		},
		function(event){
			jQuery(this).removeClass('ui-state-hover');
		}
	);
}