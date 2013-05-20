/**
 * Viewport
 */
Ext.define('ScalextApp.view.Viewport', {

    extend: 'Ext.container.Viewport',

    layout: {
        type: 'border',
        padding: '0 5 5 5'
    },

    items: [
        {
            id: 'app-header',
            xtype: 'box',
            region: 'north',
            height: 40,
            html: 'Scalext Example'
        },
        {
            xtype: 'tabpanel',
            region: 'center',
            plain: true,
            layout:'fit',
            items: [
                {
                    title: 'Direct',
                    xtype: 'directTab'
                }
            ]
        }
    ]
});