/**
 * Scalext
 */
Ext.application({

    name: "Scalext",

    appFolder: env.basePath + "assets/app",


    controllers: [

    ],

    models: [

    ],

    launch: function()
    {
        Ext.create("Scalext.view.Viewport");
    }
});