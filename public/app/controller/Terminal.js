Ext.define('Scalext.controller.Terminal', {

	extend: 'Ext.app.Controller',

	websocketUrl: "ws://localhost:9000/live",


	refs : [
		{
			ref: 'terminal',
			selector: 'terminal'
		}
	],

	init: function()
	{
		var me = this;

		this.control({
			'terminal' : {
				execute: this.handleExecute
			}
		});


        var websocket = new WebSocket(this.websocketUrl);
        websocket.onopen = function (ev) {

        };
        websocket.onclose = function (ev) {

        };
        websocket.onmessage = function(ev) {
            var data = JSON.parse(ev.data);
		//console.log(ev.data);

            if (data.message) {
                me.getTerminal().writeLine(data.message);
            }
        };
        websocket.onerror = function (ev) {

        };

        this.websocket = websocket;
	},

	/**
	 * Handle executions
	 */
	handleExecute: function(terminal, cmd)
	{
		if (!cmd.isHandled()) {
			TerminalCtrl.execute(cmd.getCommand(), function(result){
				terminal.writeLine(result);
			});
			cmd.handle();
		}
	}
});
