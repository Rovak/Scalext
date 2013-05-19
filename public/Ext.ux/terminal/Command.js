/**
 * Command fired by Terminal
 */
Ext.define('Ext.ux.terminal.Command', {

    terminal: null,

    /**
     * Set the terminal which fired the command
     *
     * @param {Ext.ux.Terminal} terminal
     */
    setTerminal: function(terminal)
    {
        this.terminal = terminal;
    },

    /**
     * @return {Ext.ux.Terminal} terminal which fired this command
     */
    getTerminal: function()
    {
        return this.terminal;
    },

    /**
     * Full command text
     *
     * @param {String} value
     */
    setCommand: function(value)
    {
        this.value = value;
    },

    /**
     * Retrieve a command as text when no index is given it will
     * return the full command
     *
     * @param {Strin} index
     * @return {String}
     */
    getCommand: function(index)
    {
        if (typeof index === 'undefined') {
            return this.value;
        } else if (parseInt(index, 0) >= 0) {
            return this.getCommands()[index];
        }

        return null;
    },

    /**
     * Commands
     *
     * @return {Array}
     */
    getCommands: function()
    {
        return typeof this.value === 'string' ? this.value.split(' ') : [];
    },

    /**
     * Is the command handled?
     *
     * @return {Boolean}
     */
    isHandled: function()
    {
        return !!this.handled;
    },

    /**
     * Set the command as handled
     */
    handle: function()
    {
        this.handled = true;
    }
});