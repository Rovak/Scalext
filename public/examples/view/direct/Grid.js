Ext.define('ScalextApp.view.direct.Grid', {

    extend: 'Ext.grid.Panel',

    alias: 'widget.directGrid',

    title: 'Company Grid',

    store: {
        remoteSort: true,
        autoLoad: true,
        sorters: [{
            property: 'name',
            direction: 'ASC'
        }],
        proxy: {
            type: 'direct',
            directFn: Application.Direct.Grid.getGrid
        },
        fields: [
            { name: 'name' },
            { name: 'info' }
        ]
    },
    columns: [
        {
            dataIndex: 'name',
            flex: 1,
            text: 'Name'
        },
        {
            dataIndex: 'info',
            flex: 1,
            text: 'Info'
        }
    ]
});