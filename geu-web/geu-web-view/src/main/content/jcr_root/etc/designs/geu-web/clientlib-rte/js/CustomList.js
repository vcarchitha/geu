(function(){
    var CustomList = {

        TCPT_FEATURE: "customlist"

    };


  //********************tool bar builder******************************************

   CUI.rte.ui.cui.CuiToolbarBuilder = new Class({

        toString: "TAEMCuiToolbarBuilder",

        extend: CUI.rte.ui.cui.CuiToolbarBuilder,

        _getUISettings: function(options) {

            var uiSettings = this.inherited(arguments)

            var items = uiSettings["inline"]["popovers"]["lists"].items;

            if(items.indexOf('customlist#circle1') == -1){
                items.push('customlist#circle1');
            }


            toolbar  = uiSettings["fullscreen"]["toolbar"];


            if(toolbar.indexOf('customlist#circle1') == -1){
                toolbar.splice(27,0, 'customlist#circle1');
            }


            return uiSettings;
        }
    });



  //**************************************extend CUI toolkit impl to create instances of extended toolbar builder and dialog manager

     CUI.rte.ui.cui.ToolkitImpl = new Class({

        toString: "TAEMCuiToolkitImpl",

        extend: CUI.rte.ui.cui.ToolkitImpl,

        createToolbarBuilder: function() {

             return new  CUI.rte.ui.cui.CuiToolbarBuilder();
        }
    });

  //******************************register the  ToolkitImpl*********************************************

    CUI.rte.ui.ToolkitRegistry.register("cui",CUI.rte.ui.cui.ToolkitImpl);


   //******************************the custom List plugin for touch ui RTE*********************************************

    CustomList.CustomListPlugin = new Class({
        toString: "CustomListPlugin",

        extend: CUI.rte.plugins.Plugin,

        circleUI: null,


        getFeatures: function () {
          return ['circle1'];
        },

       initializeUI: function(tbGenerator) {


            var plg = CUI.rte.plugins;

			if (this.isFeatureEnabled('circle1')) {

               this.circleUI = tbGenerator.createElement('circle1', this, false,this.getTooltip('circle1'));

               tbGenerator.addElement('customlist', plg.Plugin.SORT_LISTS, this.circleUI, 40);

               tbGenerator.registerIcon('customlist#circle1', "textBulletedHierarchy");
           }

        },


        execute: function(id) {

        var value = undefined;

           if (id == "circle1") {
               
               value = this.config.keepStructureOnUnlist;
               
           }
			 this.editorKernel.relayCmd(id, value);
        },

        updateState: function(selDef) {

           var context = selDef.ditContext;
           var state, isDisabled;
           

            if (this.circleUI) {
               state = this.editorKernel.queryState('circle1', selDef);
               isDisabled = (state === null || state === undefined) ||
                   (state === CUI.rte.commands.List.NO_LIST_AVAILABLE);
               this.circleUI.setSelected((state === true) || (state === null || state === undefined));
               this.circleUI.setDisabled(isDisabled);
           }

        }


    });

    //*****************************************rigister the CustomListPlugin *********************************

    CUI.rte.plugins.PluginRegistry.register('customlist',CustomList.CustomListPlugin);


    //*****************************************the command for making list logic*********************************


    CustomList.CustomListCmd = new Class({

        toString: "CustomListCmd",

        extend: CUI.rte.commands.Command,

        isCommand: function(cmdStr) {

            var cmdStrLC = cmdStr.toLowerCase();
            return (cmdStrLC == "circle1") ;
        },


        getProcessingOptions: function() {
            var cmd = CUI.rte.commands.Command;
            return cmd.PO_SELECTION | cmd.PO_BOOKMARK | cmd.PO_NODELIST;
        },

        getStyleType: function(listStyleType) {
            
            return {
                "list-style-type": listStyleType

            };
        },

        /**
        * Gets all list items of the current selection. Using this method will not include
        * items of a nested list if a nested list is completely covered in the selection.
        * @private
        */
        getListItems: function(execDef) {
            var context = execDef.editContext;
            return execDef.nodeList.getTags(context, [ {
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "li"),
                        "preventRecursionIfMatching": true
                    };
                }
            }
                                                     ], true, true);
        },
        
        /**
        * Gets all list items of the current selection. Using this method will include
        * items of a nested list as well.
        * @private
        */
        getAllListItems: function(execDef) {
            var context = execDef.editContext;
            var allItems = execDef.nodeList.getTags(context, [ {
                "matcher": function(dom) {
                    return CUI.rte.Common.isTag(dom, "li");
                }
            }
                                                             ], true, true);
            CUI.rte.ListUtils.postprocessSelectedItems(allItems);
            return allItems;
        },

        /**
        * Gets the defining list element for the specified node list. The defining list element
        * is the list element that belongs to the first node contained in the list.
        * @param {CUI.rte.EditContext} context The edit context
        * @param {CUI.rte.NodeList} nodeList The node list
        * @return {HTMLElement} The defining list DOM; null if the first node of the list
        *         is not part of a list
        */
        getDefiningListDom: function(context, nodeList) {
            var com = CUI.rte.Common;
            var determNode = nodeList.getFirstNode();
            if (determNode == null) {
                return null;
            }
            var determDom = determNode.dom;
            while (determDom) {
                if (com.isTag(determDom, com.LIST_TAGS)) {
                    return determDom;
                }
                determDom = com.getParentNode(context, determDom);
            }
            return null;
        },

        /**
        * Splits the specified array of list items into separate arrays of items for each
        * top-level list.
        * @private
        */
        splitToTopLevelLists: function(execDef, listItems) {
            var context = execDef.editContext;
            var itemsPerList = [ ];
            var topLevelLists = [ ];
            var itemCnt = listItems.length;
            for (var i = 0; i < itemCnt; i++) {
                var itemToCheck = listItems[i];
                var listDom = CUI.rte.ListUtils.getTopListForItem(context, itemToCheck.dom);
                var listIndex = CUI.rte.Common.arrayIndex(topLevelLists, listDom);
                if (listIndex < 0) {
                    topLevelLists.push(listDom);
                    itemsPerList.push([ itemToCheck ]);
                } else {
                    itemsPerList[listIndex].push(itemToCheck);
                }
            }
            return itemsPerList;
        },

        /*********************Changes the list type of all selected list items, inserting additional tables*************************************************

        /**
        * Changes the list type of all selected list items, inserting additional tables
        * as required.
        * @private
        */
        changeItemsListType: function(execDef, listItems, listStyleType) {

            var listType ="ul";
            var com = CUI.rte.Common;
            var context = execDef.editContext;
            var itemCnt = listItems.length;           
		    var style = "list-style-type:"+listStyleType;            

            for (var i = 0; i < itemCnt; i++) {
                var item = listItems[i].dom;
                var list = item.parentNode;
                if (!com.isTag(list, listType)) {
                    // Change item ...
                    var prevSib = list.previousSibling;
                    var nextSib = list.nextSibling;
                    var isFirst = (com.getChildIndex(item) == 0);
                    var isLast = (com.getChildIndex(item) == (list.childNodes.length - 1));
                    if (isFirst && prevSib && com.isTag(prevSib, listType)) {
                        // move to preceding list of correct type
                        list.removeChild(item);
                        prevSib.appendChild(item);
                        if (list.childNodes.length == 0) {
                            list.parentNode.removeChild(list);
                        }
                    } else if (isLast && nextSib && com.isTag(nextSib, listType)) {
                        // move to succeeding list of correct type
                        list.removeChild(item);
                        com.insertBefore(nextSib, item, nextSib.firstChild);
                        if (list.childNodes.length == 0) {
                            list.parentNode.removeChild(list);
                        }
                    } else {
                        // we need a new list
                        var newList = context.createElement(listType);
                        if (item == list.firstChild) {
                            // create new list before existing list
                            com.insertBefore(list.parentNode, newList, list);
                        } else if (item == list.lastChild) {
                            // create new list after existing list
                            com.insertBefore(list.parentNode, newList, list.nextSibling);
                        } else {
                            // split list
                            var splitList = list.cloneNode(false);
                            com.insertBefore(list.parentNode, splitList, list);
                            com.insertBefore(list.parentNode, newList, list);
                            while (list.childNodes[0] != item) {
                                var domToMove = list.childNodes[0];
                                list.removeChild(domToMove);
                                splitList.appendChild(domToMove);
                            }
                        }
                        list.removeChild(item);
                        newList.appendChild(item);
                        if (list.childNodes.length == 0) {
                            list.parentNode.removeChild(list);
                        }
                    }
                    
                }
            }

                 //com.setAttribute(newList, "style",style);
             if(listStyleType == "circle1"){
                com.setAttribute(newList, "class", listStyleType);
            }else{
				com.setAttribute(newList, "style",style);
            }

        },

        /*********************this method is called only to create list first time ********************************************/


        createList: function(context, blockList, listStyleType) {

            var listType = "ul";
            var com = CUI.rte.Common;
            var dpr = CUI.rte.DomProcessor;
            var lut = CUI.rte.ListUtils;
			var style = "list-style-type:"+listStyleType;

            // preprocess if a table cell is reported as the only edit block
            if ((blockList.length == 1) && com.isTag(blockList[0], com.TABLE_CELLS)) {
                var tempBlock = context.createElement("div");
                com.moveChildren(blockList[0], tempBlock);
                blockList[0].appendChild(tempBlock);
                blockList[0] = tempBlock;
            }
            // simplify block list by using lists instead of their respective list items
            var blockCnt = blockList.length;
            for (var b = 0; b < blockCnt; b++) {
                if (com.isTag(blockList[b], "li")) {
                    var listNode = blockList[b].parentNode;
                    blockList[b] = listNode;
                    for (var b1 = 0; b1 < b; b1++) {
                        if (blockList[b1] == listNode) {
                            blockList[b] = null;
                            break;
                        }
                    }
                }
            }
            // common list creation
            var listDom = context.createElement("ul");

            //com.setAttribute(listDom, "style",style);

            if(listStyleType == "circle1"){
                com.setAttribute(listDom, "class",listStyleType);
            }else{
				com.setAttribute(listDom, "style",style);
            }

            blockCnt = blockList.length;
            for (b = 0; b < blockCnt; b++) {
                var blockToProcess = blockList[b];

                if (blockToProcess) {
                    var mustRecurse = com.isTag(blockToProcess, dpr.AUXILIARY_ROOT_TAGS);
                    if (!mustRecurse) {
                        if (listDom.childNodes.length == 0) {
                            // first, insert the list
                            blockToProcess.parentNode.insertBefore(listDom, blockToProcess);
                        }
                        if (!com.isTag(blockToProcess, com.LIST_TAGS)) {
                            // normal blocks
                            var listItemDom = context.createElement("li");
                            listDom.appendChild(listItemDom);

                            com.moveChildren(blockToProcess, listItemDom, 0, true);
                            blockToProcess.parentNode.removeChild(blockToProcess);
                        } else {
                            // pre-existing list
                            com.moveChildren(blockToProcess, listDom, 0, true);
                            blockToProcess.parentNode.removeChild(blockToProcess);
                        }
                    } else {
                        // create list recursively
                        var subBlocks = [ ];
                        var sbCnt = blockToProcess.childNodes.length;
                        for (var c = 0; c < sbCnt; c++) {
                            var subBlock = blockToProcess.childNodes[c];
                            if (com.isTag(subBlock, com.EDITBLOCK_TAGS)) {
                                subBlocks.push(subBlock);
                            } else if (com.isTag(com.BLOCK_TAGS)) {
                                // todo nested tables
                            }
                        }
                        if (subBlocks.length == 0) {
                            subBlocks.push(blockToProcess);
                        }
                        lut.createList(context, subBlocks, listType);
                        // start a new list if a non-listable tag has been encountered
                        listDom = context.createElement(listType);

                    }
                }
            }
            // check if we can join adjacent lists
            var prevSib = listDom.previousSibling;
            if (prevSib && com.isTag(prevSib, listType)) {
                com.moveChildren(listDom, prevSib, 0, true);
                listDom.parentNode.removeChild(listDom);
                listDom = prevSib;
            }
            var nextSib = listDom.nextSibling;
            if (nextSib && com.isTag(nextSib, listType)) {
                com.moveChildren(nextSib, listDom, 0, true);
                nextSib.parentNode.removeChild(nextSib);
            }
        },

    /*********************Creates a new list from all (allowed) block nodes defined in the selection.********************************************

    /**
     * Creates a new list from all (allowed) block nodes defined in the selection.
     * @private
     */
        createListFromSelection: function(execDef, listStyleType) {

            var nodeList = execDef.nodeList;
            var listType="ul";
            var context = execDef.editContext;
            var tagObj = this.getStyleType(listStyleType);
            var blockLists = nodeList.getEditBlocksByAuxRoots(context, true);
            var listCnt = blockLists.length;

            for (var l = 0; l < listCnt; l++) {
                this.createList(context, blockLists[l], listStyleType)

            }
        },

       /********************* Removes items from a list by appending them to their respective parent item********************************************

        /**
        * Removes items from a list by appending them to their respective parent item
        * (including a separating "br" line break).
        * @private
        */
        unlistItems: function(execDef, listItems, keepStructure) {
            if (!listItems) {
                listItems = this.getAllListItems(execDef);
            }
            var context = execDef.editContext;
            var itemCnt = listItems.length;
            var itemsDom = [ ];
            for (var i = 0; i < itemCnt; i++) {
                itemsDom.push(listItems[i].dom);
            }
            CUI.rte.ListUtils.unlistItems(context, itemsDom, keepStructure);
        },



        /*********************this method is called only when we click on the icon********************************************/


        execute: function(execDef) {
            

            var com = CUI.rte.Common;
            var context = execDef.editContext;
            
            var nodeList = execDef.nodeList;
            var command = execDef.command;
            var tagObj = this.getStyleType(command);
            var value = execDef.value;
            var listType="ul";
            var listStyleType = null;
            //nodeList.commonAncestor.firstChild.surround(execDef.editContext, tagObj.tag, tagObj.attributes);
            switch (command.toLowerCase()) {
                case "circle1":
                    listStyleType = "circle1";
                    break;    
                    
            }
            //
            if (listType) {
                var listItems;
                var refList = this.getDefiningListDom(context, nodeList);
                var classValue = com.getAttribute(refList,"class", true);
                var stylevalue=com.getAttribute(refList, "style", true);
                // var commandcheck = command+";";
                if(stylevalue!=null){
                    var propNameValue = stylevalue.split(':');
                    var propNameValue1 = propNameValue[1];                   

                }
                else{
					var propNameValue1 = classValue;
                }
                
                if (refList == null) {
                    this.createListFromSelection(execDef, listStyleType);
                }else if (!com.isTag(refList, listType)){
                   listItems = this.getListItems(execDef);
                    this.changeItemsListType(execDef, listItems, listStyleType);
                    
                }else if (!(command == propNameValue1)) {
                   var style = "list-style-type:"+listStyleType;
                    com.setAttribute(refList, "style",style);
                    if(command == 'circle1'){                            
                            com.removeAttribute(refList, "style");
                            com.setAttribute(refList, "class",command);
                    }
                    if(propNameValue1 == 'circle1'){                            
                            com.removeAttribute(refList, "class");
                    }
                 }else {
                    // unlist all items of lead list
                    listItems = this.getAllListItems(execDef);

                    if (listItems.length > 0) {
                        var itemsByList = this.splitToTopLevelLists(execDef, listItems);
                        var listCnt = itemsByList.length;
                        for (var l = 0; l < listCnt; l++) {
                            listItems = itemsByList[l];
                            this.unlistItems(execDef, listItems, value === true);
                        }
                    }
                }
            }
        }

    });

     //************************************register CustomListCmd command*****************************

    CUI.rte.commands.CommandRegistry.register("customlist",CustomList.CustomListCmd);

})();
