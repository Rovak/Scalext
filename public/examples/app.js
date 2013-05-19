Ext.application({

    name: 'ScalextApp',

    appFolder: env.basePath + 'assets/examples',

    controllers: [
        'Direct'
        //,'Service'
    ],

    launch: function()
    {
        Ext.create('ScalextApp.view.Viewport');
    }
});