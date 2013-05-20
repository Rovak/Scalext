Ext.define('ScalextApp.view.direct.Tab', {

    extend: 'Ext.tab.Panel',

    alias: 'widget.directTab',

    layout: 'fit',

    items: [
        { xtype: 'directForm',          title: 'Form' }
        ,{ xtype: 'directFileupload',    title: 'Fileupload' }
        //,{ xtype: 'directGrid',          title: 'Grid' }
        //,{ xtype: 'directNamed',         title: 'Named Arguments' }
        //,{ xtype: 'directTree',          title: 'Tree' }
    ]
});