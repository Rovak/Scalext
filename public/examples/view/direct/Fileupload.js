/**
 * Upload file example
 */
Ext.define('ScalextApp.view.direct.Fileupload', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.directFileupload',

    bodyPadding: 5,

    initComponent: function()
    {
        var basicInfo = Ext.create('Ext.form.Panel', {
            // configs for FormPanel
            title: 'Upload files',
            border: false,
            bodyPadding: 10,
            api: {
                // The server-side must mark the submit handler as a 'formHandler'
                submit: Application.Direct.form.Upload.uploadFile
            },
            defaultType: 'fileuploadfield',
            defaults: {
                anchor: '100%'
            },
            items: [
                {
                    fieldLabel: 'File 1',
                    name: 'file_1'
                },
                {
                    fieldLabel: 'File 2',
                    name: 'file_2'
                },
                {
                    fieldLabel: 'File 3',
                    name: 'file_3'
                }
            ],
            // specify the order for the passed params
            dockedItems: [{
                dock: 'bottom',
                xtype: 'toolbar',
                ui: 'footer',
                style: 'margin: 0 5px 5px 0;',
                items: [
                    '->',
                    {
                        text: 'Submit',
                        handler: function() {
                            basicInfo.getForm().submit({
                                success: function(form, action) {
                                    Ext.Msg.alert('Success', action.result.msg);
                                 },
                                 failure: function(form, action) {
                                    Ext.Msg.alert('Failure', action.result.msg);
                                 }
                             });
                        }
                    }
                ]
            }],
        });

        this.items = [{
            xtype: 'panel',
            width: 300,
            items: [
                basicInfo
            ]
        }];

        this.callParent(arguments);
    }
});