/**
 * Simple Terminal
 */
Ext.define('Ext.ux.Terminal', {

    extend: 'Ext.Panel',

    xtype: 'terminal',

    bodyStyle: {
        'background-color': '#000;',
        'color': '#FFF',
        'font-family' : 'Courier New',
        'padding' : '5px',
        'overflow-y': 'scroll'
    },

    requires: [
        'Ext.ux.terminal.Command'
    ],

    /**
     * Input element
     *
     * @type {Ext.dom.Element}
     */
    inputElement: null,

    /**
     * Command line element
     *
     * @type {Ext.dom.Element}
     */
    consoleElement: null,

    /**
     * Cursor
     *
     * @type {String}
     */
    cursor: '&gt;',

    initComponent: function()
    {
        var me = this;

        Ext.applyIf(this, {
            html: '<div class="console"></div>' +
                '<div style="white-space: nowrap;">' +
                    this.cursor +
                    ' <input class="cursor" spellcheck="false"' +
                    ' type="text" style="width: 90%; background-color: #000; color: #FFF; padding: 0;' +
                    ' margin: 0; display: inline; font-family: Courier New; border: 0;"></input>' +
                '</div>'
        });

        this.on({
            render: function() {
                me.consoleElement = Ext.select('.console', false, me.getEl().dom).item(0);
                me.inputElement = Ext.select('.cursor', false, me.getEl().dom).item(0);
                me.inputElement.on('keydown', function(ev) {
                    switch (ev.getKey()) {
                        case ev.ENTER:
                            me.onEnterKey(ev);
                            break;

                        // HANDLE HISTORY
                        case ev.UP:
                            ev.preventDefault();
                            break;
                    }
                });

                me.onStart();
            },
            execute: function(terminal, cmd)
            {
                me.handleCommand(cmd);
            }
        });

        this.callParent();
    },

    /**
     * Handles build in functions
     *
     * @param {Ext.ux.terminal.Command} cmd
     */
    handleCommand: function(cmd)
    {
        switch(cmd.getCommand(0))
        {
            case "color":
                cmd.handle();
                var color = cmd.getCommand(1);
                if (!color) {
                    cmd.getTerminal().writeLine("Error: invalid color");
                    return;
                }
                this.inputElement.setStyle('color', color);
                this.body.setStyle('color', color);
                cmd.getTerminal().writeLine('changed color to ' + color);
                break;
            case "bgcolor":
            case "backgroundcolor":
                cmd.handle();
                var color = cmd.getCommand(1);
                if (!color) {
                    cmd.getTerminal().writeLine("Error: invalid color");
                    return;
                }
                this.inputElement.setStyle('background-color', color);
                this.body.setStyle('background-color', color);
                cmd.getTerminal().writeLine('Changed background to ' + color);
                break;
            case "time":
            case "date":
            case "datetime":
                cmd.handle();
                cmd.getTerminal().writeLine((new Date()).toLocaleString());
                break;
            case "cls":
                cmd.handle();
                cmd.getTerminal().clearScreen();
                break;
        }
    },

    /**
     * When the terminal starts
     */
    onStart: function()
    {
        this.writeLine('Welcome to ' + document.title);
        this.writeLine((new Date()).toLocaleString());
        this.fireEvent('start', this);
    },

    /**
     * Write text to the console
     *
     * @param {String} text
     */
    write: function(text)
    {
        this.consoleElement = this.consoleElement.setHTML(this.consoleElement.getHTML() + text);
        this.fireEvent('write', this, text);
        this.body.dom.scrollTop = this.body.dom.scrollHeight;
    },

    /**
     * Write text with to the console and start a new line
     *
     * @param {String} text
     */
    writeLine: function(text)
    {
        this.write(text + '<br>');
    },

    /**
     * Retrieve all console lines
     *
     * @return {Array}
     */
    getConsoleLines: function()
    {
        return this.consoleElement.getHTML().split('<br>');
    },

    /**
     * Clear the HTML screen
     */
    clearScreen: function()
    {
        this.consoleElement.setHTML('');
    },

    /**
     * Return the text of the current line
     *
     * @return {String}
     */
    getCommandLineValue: function()
    {
        return this.inputElement.dom.value;
    },

    /**
     * Set the current command line value
     *
     * @param {String} value
     */
    setCommandLineValue: function(value)
    {
        this.inputElement.dom.value = value;
    },

    /**
     * Handles on enter
     *
     * @param  {Ext.EventObject} ev
     */
    onEnterKey: function(ev)
    {
        var line = this.getCommandLineValue();
        this.onExecute(line);
        this.inputElement.dom.value = '';
        ev.preventDefault();
    },

    /**
     * Execute current command
     *
     * @param  {String} command
     */
    onExecute: function(command)
    {
        var cmd = Ext.create('Ext.ux.terminal.Command');
        cmd.setCommand(command);
        cmd.setTerminal(this);

        this.writeLine(this.cursor + ' ' + command);
        this.fireEvent('execute', this, cmd);

        if (!cmd.isHandled()) {
            this.writeLine("Error: " + command + " not handled");
        }
    }
});