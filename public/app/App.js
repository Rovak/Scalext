/**
 * Scalext
 */
Ext.application({

    name: "Scalext",

    appFolder: env.basePath + "assets/app",

    requires: [
        'Ext.ux.Terminal',
        'MyDesktop.App'
    ],

    controllers: [
        'Terminal'
    ],

    models: [

    ],

    launch: function()
    {
        myDesktopApp = new MyDesktop.App();
        Ext.create('Ext.Window', {
            layout: 'fit',
            title: 'Terminal',
            width: 600,
            height: 400,
            items: [
                { xtype: 'terminal' }
            ],
            listeners: {
                activate: function(ev) {
                    this.getEl().down('.cursor').focus();
                }
            }
        }).show();
    }
});