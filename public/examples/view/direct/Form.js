/**
 * Direct Form Example
 */
Ext.define('ScalextApp.view.direct.Form', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.directForm',

    bodyPadding: 5,

    initComponent: function()
    {
        var basicInfo = Ext.create('Ext.form.Panel', {
            // configs for FormPanel
            title: 'Basic Information',
            border: false,
            bodyPadding: 10,
            // configs for BasicForm
            api: {
                // The server-side method to call for load() requests
                load: Scalext.example.Profile.getBasicInfo,
                // The server-side must mark the submit handler as a 'formHandler'
                submit: Scalext.example.Profile.updateBasicInfo
            },
            // specify the order for the passed params
            dockedItems: [{
                dock: 'bottom',
                xtype: 'toolbar',
                ui: 'footer',
                style: 'margin: 0 5px 5px 0;',
                items: ['->', {
                    text: 'Submit',
                    handler: function(){
                        basicInfo.getForm().submit();
                    }
                }]
            }],
            defaultType: 'textfield',
            defaults: {
                anchor: '100%'
            },
            items: [{
                fieldLabel: 'Name',
                name: 'name'
            },{
                fieldLabel: 'Email',
                msgTarget: 'side',
                name: 'email'
            },{
                fieldLabel: 'Company',
                name: 'company'
            }]
        });

        var phoneInfo = Ext.create('Ext.form.Panel', {
            title: 'Phone Numbers',
            border: false,
            api: {
                load: 'Scalext.example.Profile.getPhoneInfo'
            },
            bodyPadding: 10,
            defaultType: 'textfield',
            defaults: {
                anchor: '100%'
            },
            items: [{
                fieldLabel: 'Office',
                name: 'office'
            },{
                fieldLabel: 'Cell',
                name: 'cell'
            },{
                fieldLabel: 'Home',
                name: 'home'
            }]
        });

        var locationInfo = Ext.create('Ext.form.Panel', {
            title: 'Location Information',
            border: false,
            bodyPadding: 10,
            api: {
                load: 'Scalext.example.Profile.getLocationInfo'
            },
            defaultType: 'textfield',
            defaults: {
                anchor: '100%'
            },
            items: [{
                fieldLabel: 'Street',
                name: 'street'
            },{
                fieldLabel: 'City',
                name: 'city'
            },{
                fieldLabel: 'State',
                name: 'state'
            },{
                fieldLabel: 'Zip',
                name: 'zip'
            }]
        });


        this.items = [{
            layout: 'accordion',
            xtype: 'panel',
            title: 'My Profile',
            width: 300,
            height: 240,
            items: [basicInfo, phoneInfo, locationInfo]
        }];

        this.callParent();

        //window.basicInfo = basicInfo;
        basicInfo.getForm().load();
        //phoneInfo.getForm().load();
        //locationInfo.getForm().load();
    }
});