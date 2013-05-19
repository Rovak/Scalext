/**
 * Direct Named Arguments Example
 *
 * @see http://docs.sencha.com/ext-js/4-1/#!/example/direct/named-arguments.html
 */
Ext.define('ScalextApp.view.direct.Named', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.directNamed',

    bodyPadding: 5,

    initComponent: function()
    {
        var form = Ext.create('Ext.form.Panel', {
            width: 300,
            height: 130,
            bodyPadding: 5,
            items: [{
                xtype: 'textfield',
                fieldLabel: 'First Name',
                name: 'firstName',
                value: 'Roy',
                allowBlank: false,
                maxLength: 30,
                enforceMaxLength: true
            }, {
                xtype: 'textfield',
                fieldLabel: 'Last Name',
                name: 'lastName',
                value: 'van Kaathoven',
                allowBlank: false,
                maxLength: 30,
                enforceMaxLength: true
            }, {
                xtype: 'numberfield',
                fieldLabel: 'Age',
                name: 'age',
                value: 24,
                allowBlank: false
            }],
            dockedItems: [{
                dock: 'bottom',
                ui: 'footer',
                xtype: 'toolbar',
                items: ['->', {
                    formBind: true,
                    text: 'Send',
                    handler: function(){
                        var values = form.getForm().getValues();
                        Application.Direct.NamedArguments.showDetails(values, function(value){
                            Ext.example.msg('Server Response', value);
                        });
                    }
                }]
            }]
        });


        this.items = [form];

        this.callParent(arguments);
    }
});