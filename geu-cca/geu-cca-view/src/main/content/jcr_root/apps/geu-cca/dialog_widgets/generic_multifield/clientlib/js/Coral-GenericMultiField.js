//coralGMF stands for Coral-GenericMultiField
var coralGMF = {

		addDataInFields : function(mValues, mName) {
            var $fieldSets = $("[class='coral-Form-fieldset'][data-name='" + mName + "']");

            var record, $fields, $field, name;
 
            $fieldSets.each(function (i, fieldSet) {
                $fields = $(fieldSet).find("[name]");
                record = mValues[i];

				//return if empty
                if (!record) return;

                $fields.each(function (j, field) {
                    $field = $(field);

                    name = $field.attr("name");
 
					//return is Name attribute is missing
                    if (!name) return;

                    //strip ./
                    if (name.indexOf("./") == 0) {
                        name = name.substring(2);
                    }

                    // NOTE: add your case for spacial fields, KEEP DEFAULT AS THE LAST OPTION
                    switch ($field[0].className) {
                        case "coral-Checkbox-input":
                            coralCBX.addDataInField(record[name],$field);
                            break;
                        default:
                            $field.val(record[name]);
                    }
                });
            });
        },
 
        //collect data from widgets in multifield and POST them to CRX as JSON
		collectDataFromFields : function(mName){

            $('.endor-ActionBar-item[title="Done"]').removeAttr('type');

            $(document).on("click", "[title='Done']", function (e) {

                var $form;
                if($(this).attr('form')!=undefined && $(this).attr('form').length > 0) {
                    var formId = $(this).attr('form');
                    $form = $('#propertiesform');
                } else {
                    $form = $(this).closest("form.foundation-form");
                }
                //get all the input fields of multifield
                var $fieldSets = $("[class='coral-Form-fieldset'][data-name='" + mName + "']");
                var record, $fields, $field, name; 
                var minSize = $fieldSets.attr('data-min-size');

                var $section = $("section[data-name='./links']");

                if (minSize && $fieldSets.length < minSize) {
                    e.preventDefault();
					e.stopPropagation();
					
                	var fui = $(window).adaptTo("foundation-ui");
                	fui.prompt("Error", 
                			"Es sind mindestens " + minSize + " Items benötigt.", 
                			"error",
                            [{
                                id: "OK",
                                text: "OK",
                                className: "coral-Button"
                            }],
                            function (actionId) {
                				if (actionId == "OK") {}
                            }
                        );

                } else if ($section.find("[invalid='true']").length > 0) {

                } else {

                $fieldSets.each(function (i, fieldSet) {
                    $fields = $(fieldSet).find("[name]");
                    record = {};
 
                    $fields.each(function (j, field) {
                        $field = $(field); 
                        name = $field.attr("name");

                        if (!name) {
                            return;
                        }
 
                        //strip ./
                        if (name.indexOf("./") == 0) {
                            name = name.substring(2);
                        }

                        // NOTE: add your case for spacial fields, KEEP DEFAULT AS THE LAST OPTION
                        switch ($field[0].className) {
                            case "coral-Checkbox-input":
                                record[name] = coralCBX.collectDataFromField($field);
                                break;
                            default:
                                record[name] = $field.val();
                        }

                        //remove field to avoid POST of single values.
                        $field.remove();
                    });
 
                    if ($.isEmptyObject(record)) {
                        return;
                    }

                    //add the record JSON in a hidden field as string
                    $('<input />').attr('type', 'hidden')
                            .attr('name', mName)
                            .attr('value', JSON.stringify(record))
                            .appendTo($form);
                });
                }
            });
        }
};