Ext.define('Scalext.view.Viewport', {

    extend: 'Ext.container.Viewport',

    layout: 'border',

    items: [
        {
            xtype: 'panel',
            html: 'test',
            region: 'center'
        }
    ]
});