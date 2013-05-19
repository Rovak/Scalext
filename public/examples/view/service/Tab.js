Ext.define('ScalextApp.view.service.Tab', {

    extend: 'Ext.tab.Panel',

    alias: 'widget.serviceTab',

    layout: 'fit',

    items: [
        { xtype: 'serviceGrid', title: 'Grid' }
    ]
});