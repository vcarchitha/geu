(function() {

    var CustomStyles = {};

  //******************************CUI.rte.ui.cui.CuiToolbarBuilder*********************************************


    CUI.rte.ui.cui.CuiToolbarBuilder = new Class({

        toString: "CustomStylesCuiToolbarBuilder",

        extend: CUI.rte.ui.cui.CuiToolbarBuilder,

        _getUISettings: function(options) {

            var uiSettings = this.superClass._getUISettings(options);

            var toolbar = uiSettings["fullscreen"]["toolbar"];
            var popovers = uiSettings["fullscreen"]["popovers"];

            // Font Styles
            if (toolbar.indexOf("#customstyles") == -1) {

                toolbar.splice(10, 0, "#customstyles");
            }
            if (!popovers.hasOwnProperty("customstyles")) {
                popovers.customstyles = {
                    "ref": "customstyles",
                    "items": "customstyles:getStyles:customstyles-pulldown"
                };
            }

            return uiSettings;
        },

        // Styles dropdown builder
        createCustomStyleSelector: function(id, plugin, tooltip, styles) {


            return new CustomStyles.CustomStylesSelectorImpl(id, plugin, false, tooltip, false, undefined, styles);

        }

    });

    //**************************************extend CUI toolkit impl to create instances of extended toolbar builder and dialog manager

    CUI.rte.ui.cui.ToolkitImpl = new Class({

        toString: "customstylesToolkitImpl",

        extend: CUI.rte.ui.cui.ToolkitImpl,

        createToolbarBuilder: function() {
            return new CUI.rte.ui.cui.CuiToolbarBuilder();
        }
    });

    //******************************register the  ToolkitImpl*********************************************

    CUI.rte.ui.ToolkitRegistry.register("cui", CUI.rte.ui.cui.ToolkitImpl);



    /*********************************************************TableStyleSelectorImpl*********************************/


    CustomStyles.CustomStylesSelectorImpl = new Class({

        toString: 'CustomStylesSelectorImpl',

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

                        self.plugin.execute('customstyles', style);
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

    CustomStyles.CustomStylesPlugin = new Class({

        toString: "CustomStylesPlugin",

        extend: CUI.rte.plugins.Plugin,

        /**
         * @private
         */
        customstylescachedStyles: null,

        /**
         * @private
         */
        customstylesUI: null,
        getFeatures: function() {

            return ['customstyles'];
        },

        reportStyles: function() {
            return [{
                'type': 'text',
                'customstyles': this.getStyles()
            }];
        },

        /**
         * @private
         */
        getStyleId: function(dom) {
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
            if (!this.customstylescachedStyles) {
                this.customstylescachedStyles = this.config.customstyles || {};

                if (this.customstylescachedStyles) {
                    // take styles from config
                    com.removeJcrData(this.customstylescachedStyles);
                    this.customstylescachedStyles = com.toArray(this.customstylescachedStyles, 'cssName', 'text');

                } else {
                    this.customstylescachedStyles = [];

                }
            }

            return this.customstylescachedStyles;
        },

        setStyles: function(styles) {
            this.customstylescachedStyles = styles;
        },

        hasStylesConfigured: function() {
            return !!this.config.styles;
        },

        initializeUI: function(tbGenerator, options) {
            var plg = CUI.rte.plugins;
            if (this.isFeatureEnabled('customstyles')) {
                this.customstylesUI = new tbGenerator.createCustomStyleSelector('customstyles', this, null,
                    this.getStyles());
                tbGenerator.addElement('customstyles', '380', this.customstylesUI, 10);
            }
            tbGenerator.registerIcon('#customstyles', 'addCircle');
        },

        notifyPluginConfig: function(pluginConfig) {

            pluginConfig = pluginConfig || {};
            CUI.rte.Utils.applyDefaults(pluginConfig, {});
            this.config = pluginConfig;

        },

        execute: function(cmdId, styleDef) {

            if (!this.customstylesUI) {
                return;
            }
            var cmd = null;

            var className;

            cmd = cmdId.toLowerCase();

            className = ((styleDef !== null && styleDef !== undefined) ? styleDef : this.customstylesUI.getSelectedStyle());

            if (cmd && className) {
                this.editorKernel.relayCmd(cmd, {

                    'attributes': {
                        'class': className
                    }
                });
            }
        },

        updateState: function(selDef) {
            if (!this.customstylesUI) {
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
            this.customstylesUI.selectStyles(actualStyles, selDef);
        }
    });

    CUI.rte.plugins.PluginRegistry.register("customstyles", CustomStyles.CustomStylesPlugin);


    /*********************************************************commandImpl*************************************/

    CustomStyles.CustomStylesCommandImpl = new Class({

        toString: "CustomStylesCommandImpl",

        extend: CUI.rte.commands.Command,

        isCommand: function(cmdStr) {
            return (cmdStr.toLowerCase() == "customstyles");
        },

        getProcessingOptions: function() {
            var cmd = CUI.rte.commands.Command;
            return cmd.PO_SELECTION | cmd.PO_BOOKMARK | cmd.PO_NODELIST;
        },


        execute: function(execDef) {
            var com = CUI.rte.Common;
            var context = execDef.editContext;

            var nodeList = execDef.nodeList;
            var listItems;
            var itemCnt;
            var nodeName;
            var classname = execDef.value.attributes['class'];

            if(nodeList.nodes.length <=1){
                nodeName = execDef.nodeList.commonAncestor.nodeName.toLowerCase();
            }else{
                nodeName = execDef.nodeList.nodes[1].dom.nodeName.toLowerCase();
            }
            
           // nodeName = execDef.nodeList.commonAncestor.nodeName.toLowerCase();
            if(nodeName == "p"){
                listItems = execDef.nodeList.getTags(context, [{
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "p"),
                        "preventRecursionIfMatching": true
                    };
                }
            }], true, true);
            }else if (nodeName == "div"){
                listItems = execDef.nodeList.getTags(context, [{
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "div"),
                        "preventRecursionIfMatching": true
                    };
                }
            }], true, true);
            }else if (nodeName == "h2"){
                listItems = execDef.nodeList.getTags(context, [{
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "h2"),
                        "preventRecursionIfMatching": true
                    };
                }
            }], true, true);
            }else if (nodeName == "h3"){
                listItems = execDef.nodeList.getTags(context, [{
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "h3"),
                        "preventRecursionIfMatching": true
                    };
                }
            }], true, true);
            }else if (nodeName == "h4"){
                listItems = execDef.nodeList.getTags(context, [{
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "h4"),
                        "preventRecursionIfMatching": true
                    };
                }
            }], true, true);
            }else if (nodeName == "a"){
                listItems = execDef.nodeList.getTags(context, [{
                "extMatcher": function(dom) {
                    return {
                        "isMatching": CUI.rte.Common.isTag(dom, "a"),
                        "preventRecursionIfMatching": true
                    };
                }
            }], true, true);
            }

            var itemCnt = listItems.length;
            for (var i = 0; i < itemCnt; i++) {
               var item = listItems[i].dom;
                var classValue = com.getAttribute(item, "class", true);
                if (classValue == classname) {
                    com.removeAllClasses(item);

                } else {
                    com.setAttribute(item, "class", classname);
                }
            }

        }
    });

    CUI.rte.commands.CommandRegistry.register("customstyles", CustomStyles.CustomStylesCommandImpl);

})();