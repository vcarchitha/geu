(function() {

    var TableStyles = {};

    //******************************CUI.rte.ui.cui.CuiToolbarBuilder*********************************************


    CUI.rte.ui.cui.CuiToolbarBuilder = new Class({

        toString: "TablestylesCuiToolbarBuilder",

        extend: CUI.rte.ui.cui.CuiToolbarBuilder,

        _getUISettings: function(options) {

            var uiSettings = this.superClass._getUISettings(options);

            //add plugins to fullscreen toolbar
            var toolbar = uiSettings["fullscreen"]["toolbar"];
            var popovers = uiSettings["fullscreen"]["popovers"];

            // Font Styles
            if (toolbar.indexOf("#tablestyles") == -1) {

                toolbar.splice(10, 0, "#tablestyles");
            }
            if (!popovers.hasOwnProperty("tablestyles")) {
                popovers.tablestyles = {
                    "ref": "tablestyles",
                    "items": "tablestyles:getStyles:tablestyles-pulldown"
                };
            }

            return uiSettings;
        },

        // Styles dropdown builder
        createTableStyleSelector: function(id, plugin, tooltip, styles) {

            return new TableStyles.TablestylesSelectorImpl(id, plugin, false, tooltip, false, undefined, styles);

        }

    });

    //**************************************extend CUI toolkit impl to create instances of extended toolbar builder and dialog manager

    CUI.rte.ui.cui.ToolkitImpl = new Class({

        toString: "tablestylesToolkitImpl",

        extend: CUI.rte.ui.cui.ToolkitImpl,

        createToolbarBuilder: function() {

            return new CUI.rte.ui.cui.CuiToolbarBuilder();
        }
    });

    //******************************register the  ToolkitImpl*********************************************

    CUI.rte.ui.ToolkitRegistry.register("cui", CUI.rte.ui.cui.ToolkitImpl);


    /*********************************************************TableStyleSelectorImpl*********************************/

    TableStyles.TablestylesSelectorImpl = new Class({

        toString: 'TablestylesSelectorImpl',

        extend: CUI.rte.ui.TbStyleSelector,

        // Helpers -----------------------------------------------------------------------------

        notifyGroupBorder: function() {
            // do nothing
        },

        _getStyleId: function($button) {

            var styleId = null;
            var targetId = $button.data('action');
            var hashPos = targetId.indexOf('#');
            if (hashPos >= 0) {
                styleId = targetId.substring(hashPos + 1);
            }
            return styleId;
        },

        _resetSelection: function() {
            this.$ui.each(function() {
                this.icon = '';
            });
        },

        _select: function(styleToSelect) {
            var self = this;
            this.$ui.each(function() {
                var $fmtItem = $(this);
                var styleId = self._getStyleId($fmtItem);
                if (styleId && (styleId === styleToSelect)) {
                    this.icon = 'check';
                }
            });
        },


        // Interface implementation ------------------------------------------------------------

        addToToolbar: function(toolbar) {
            this.toolbar = toolbar;
        },

        notifyToolbar: function(toolbar, skipHandlers) {

            this.toolbar = toolbar;
            var self = this;
            var pluginId = this.plugin.pluginId;

            var $cont = toolbar.getToolbarContainer();
            var tbType = toolbar.tbType;
            var $button = $(this).find('button');

            if (!this.plugin.hasStylesConfigured()) {
                var styles = [];
                var $popover = CUI.rte.UIUtils.getPopover(pluginId, tbType, $cont);
                var $styleItems = $popover.find('li');
                $styleItems.each(function() {
                    var $button = $(this).find('button');
                    var href = $button.data('action');
                    var action = href.split('#');
                    if ((action.length === 2) && (action[0] === pluginId)) {
                        styles.push({
                            'cssName': action[1],
                            'text': $button.text()
                        });
                    }
                });
                this.plugin.setStyles(styles);
            }
            var $tbCont = CUI.rte.UIUtils.getToolbarContainer($cont, tbType);


            this.$trigger = $tbCont.find('button[data-action="#' + pluginId + '"]');
            this.$ui = $tbCont.find('button[data-action^="' + pluginId + '#"]');

            if (!skipHandlers) {

                this.$ui.on('click.rte-handler', function(e) {
                    if (!self.$ui.hasClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS)) {
                        var targetId = $(this).data('action');
                        var hashPos = targetId.indexOf('#');
                        var style = targetId.substring(hashPos + 1);
                        var editContext = self.plugin.editorKernel.getEditContext();
                        editContext.setState('CUI.SelectionLock', 1);

                        self.plugin.execute('tablestyles', style);
                        self.plugin.editorKernel.enableFocusHandling();
                        self.plugin.editorKernel.focus(editContext);
                    }
                    // e.stopPropagation();
                });
            }
        },

        createToolbarDef: function() {
            return {
                'id': this.id,
                'element': this
            };
        },

        initializeSelector: function() {
            // TODO ...?
        },

        getSelectorDom: function() {
            return this.$ui;
        },

        getSelectedStyle: function() {
            return null;
        },

        selectStyles: function(styles, selDef) {
            this.setSelected(styles.length > 0);
            this._resetSelection();
            for (var s = 0; s < styles.length; s++) {
                this._select(styles[s].className);
            }
        },

        setDisabled: function(isDisabled) {
            var com = CUI.rte.Common;
            if (com.ua.isTouchInIframe) {
                // workaround for CUI-649; see ElementImpl#setDisabled for an explanation
                this.$trigger.css('display', 'none');
            }
            if (isDisabled) {
                this.$trigger.addClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                this.$trigger.attr('disabled', 'disabled');
            } else {
                this.$trigger.removeClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                this.$trigger.removeAttr('disabled');
            }
            if (com.ua.isTouchInIframe) {
                // part 2 of workaround for CUI-649
                var self = this;
                window.setTimeout(function() {
                    self.$trigger.css('display', 'inline-block');
                }, 1);
            }
        },

        setSelected: function(isSelected, suppressEvent) {
            this._isSelected = isSelected;
            if (isSelected) {
                this.$trigger.addClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS);
            } else {
                this.$trigger.removeClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS);
            }
        },

        isSelected: function() {
            return this._isSelected;
        }


    });


    /********************************************************plugin Impl******************************************/

    TableStyles.TablestylesPlugin = new Class({

        toString: "TablestylesPlugin",

        extend: CUI.rte.plugins.Plugin,

        /**
         * @private
         */
        tablestylescachedStyles: null,

        /**
         * @private
         */
        tablestylesUI: null,

        getFeatures: function() {
            return ['tablestyles'];
        },

        reportStyles: function() {
            return [{
                'type': 'text',
                'tablestyles': this.getStyles()
            }];
        },

        /**
         * @private
         */
        getStyleId: function(dom) {
            //console.log(" inside getStyleId");
            var tagName = dom.tagName.toLowerCase();
            var styles = this.getStyles();
            var stylesCnt = styles.length;
            for (var f = 0; f < stylesCnt; f++) {
                var styleDef = styles[f];
                //TODO: We need to handle span class, not tag
                if (styleDef.tag && (styleDef.tag == tagName)) {
                    return styleDef.tag;
                }
            }
            return null;
        },

        getStyles: function() {

            var com = CUI.rte.Common;
            if (!this.tablestylescachedStyles) {
                this.tablestylescachedStyles = this.config.tablestyles || {};

                if (this.tablestylescachedStyles) {
                    // take styles from config
                    com.removeJcrData(this.tablestylescachedStyles);
                    this.tablestylescachedStyles = com.toArray(this.tablestylescachedStyles, 'cssName', 'text');

                } else {
                    this.tablestylescachedStyles = [];

                }
            }

            return this.tablestylescachedStyles;
        },

        setStyles: function(styles) {

            this.tablestylescachedStyles = styles;
        },

        hasStylesConfigured: function() {
            return !!this.config.tablestyles;
        },

        initializeUI: function(tbGenerator, options) {
            var plg = CUI.rte.plugins;
            if (this.isFeatureEnabled('tablestyles')) {

                this.tablestylesUI = new tbGenerator.createTableStyleSelector('tablestyles', this, null,
                    this.getStyles());
                tbGenerator.addElement('tablestyles', '380', this.tablestylesUI, 10);
            }
            tbGenerator.registerIcon('#tablestyles', 'tableEdit');
        },

        notifyPluginConfig: function(pluginConfig) {

            pluginConfig = pluginConfig || {};
            CUI.rte.Utils.applyDefaults(pluginConfig, {});
            this.config = pluginConfig;

        },

        execute: function(cmdId, styleDef) {

            if (!this.tablestylesUI) {
                return;
            }
            var cmd = null;
            var tagName;
            var className;
            switch (cmdId.toLowerCase()) {
                case 'tablestyles':
                    cmd = 'tablestyles';
                    tagName = 'span';
                    className = ((styleDef !== null && styleDef !== undefined) ? styleDef : this.tablestylesUI.getSelectedStyle());
                    break;
            }
            if (cmd && tagName && className) {
                this.editorKernel.relayCmd(cmd, {
                    'tag': tagName,
                    'attributes': {
                        'class': className
                    }
                });
            }
        },

        updateState: function(selDef) {
            if (!this.tablestylesUI) {
                return;
            }
            var com = CUI.rte.Common;
            var styles = selDef.startStyles;

            var actualStyles = [];
            var s;
            var styleableObject = selDef.selectedDom;
            if (styleableObject) {
                if (!CUI.rte.Common.isTag(selDef.selectedDom,
                        "img")) {
                    styleableObject = null;
                }
            }
            var stylesDef = this.getStyles();
            var styleCnt = stylesDef.length;
            if (styleableObject) {
                for (s = 0; s < styleCnt; s++) {
                    var styleName = stylesDef[s].cssName;
                    if (com.hasCSS(styleableObject, styleName)) {
                        actualStyles.push({
                            'className': styleName
                        });
                    }
                }
            } else {
                var checkCnt = styles.length;
                for (var c = 0; c < checkCnt; c++) {
                    var styleToProcess = styles[c];
                    var currentStyles = styleToProcess.className.split(" ");
                    for (var j = 0; j < currentStyles.length; j++) {
                        for (s = 0; s < styleCnt; s++) {
                            if (stylesDef[s].cssName == currentStyles[j]) {
                                actualStyles.push(currentStyles[j]);
                                break;
                            }
                        }
                    }
                }
            }
            this.tablestylesUI.selectStyles(actualStyles, selDef);
        }
    });

    CUI.rte.plugins.PluginRegistry.register("tablestyles", TableStyles.TablestylesPlugin);


    /*********************************************************commandImpl*************************************/

    TableStyles.TablestylesCommandImpl = new Class({

        toString: "TablestylesCommandImpl",

        extend: CUI.rte.commands.Command,

        isCommand: function(cmdStr) {
            return (cmdStr.toLowerCase() == "tablestyles");
        },

        _getParamsFromExecDef: function(execDef) {
            var cmdLC = execDef.command.toLowerCase();
            var isStyle = (cmdLC === "tablestyles");
            var tagName, attributes;
            if (isStyle) {
                tagName = execDef.value.tag;
                attributes = execDef.value.attributes;
            }
            return {
                "tag": tagName,
                "attributes": attributes,
                "isStyle": isStyle
            };
        },

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


        getProcessingOptions: function() {
            var cmd = CUI.rte.commands.Command;
            return cmd.PO_SELECTION | cmd.PO_BOOKMARK | cmd.PO_NODELIST;
        },

        addStyle: function(execDef) {

            var sel = CUI.rte.Selection;
            var com = CUI.rte.Common;
            var styleName = execDef.value;
            var selection = execDef.selection;
            var context = execDef.editContext;
            var def = this._getParamsFromExecDef(execDef);

            // handle DOM elements
            var selectedDom = sel.getSelectedDom(context, selection);

            var styleableObjects = "imd";
            if (selectedDom && com.isTag(selectedDom, styleableObjects)) {

                com.removeAllClasses(selectedDom);
                com.addClass(selectedDom, styleName);
                return;
            }
            // handle text fragments
            var nodeList = execDef.nodeList;
            var refList = this.getDefiningListDom(context, nodeList);
            // console.log("refList",refList);
            var determNode = nodeList.getFirstNode();

            if (nodeList) {
                var tableParent = com.getTagInPath(context, execDef.nodeList.commonAncestor, "table");
                // var listDom = context.createElement("div");
                //com.addClass(listDom,styleName);
                //com.removeAllClasses(selectedDom);
                if (!(tableParent == null)) {
                    nodeList.removeNodesByTag(execDef.editContext, def.tag, def.attributes, true);
                    if ("removestyle" === execDef.value.attributes.class) {

                        com.removeAttribute(tableParent, "class");

                    }

                    com.setAttribute(tableParent, "class", execDef.value.attributes.class);

                }
            }
        },


        execute: function(execDef) {

            switch (execDef.command.toLowerCase()) {
                case "tablestyles":
                    this.addStyle(execDef);
                    break;
            }
        },

        queryState: function(selectionDef, cmd) {
            var com = CUI.rte.Common;
            var tagName = this._getTagNameForCommand(cmd);
            if (!tagName) {
                return undefined;
            }
            var context = selectionDef.editContext;
            var selection = selectionDef.selection;
            return (com.getTagInPath(context, selection.startNode, tagName) != null);
        }
    });

    CUI.rte.commands.CommandRegistry.register("tablestyles", TableStyles.TablestylesCommandImpl);


})();