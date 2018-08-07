//coralCBX stands for Coral-Checkbox
var coralCBX = {

    addDataInField : function(recordEntry, field) {
        if(recordEntry)
        	$(field).prop('checked', true);
    },


    collectDataFromField : function(field) {
    	if($(field).is(':checked')){
            return $(field).val();
        }else{
            return "";
        }
	}


}