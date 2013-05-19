/**
 * Direct Form Example
 */
Ext.define('ScalextApp.view.direct.Tree', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.directTree',

    bodyPadding: 5,

    initComponent: function()
    {
        var store = Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true
            },
            proxy: {
                type: 'direct',
                directFn: Application.Direct.Tree.getTree,
                paramOrder: ['node']
            }
        });


        // create the Tree
        var tree = Ext.create('Ext.tree.Panel', {
            store: store,
            height: 350,
            width: 600,
            title: 'Tree Sample',
            rootVisible: false
        });

        this.items = tree;

        this.callParent(arguments);
    }
});