/**
 * Creates an Enum.
 **/
function Enumeration() {

    var args = Array.prototype.slice.call(arguments);
    
    for (var argI in args) {
       
        var arg = args[argI];
        
        if (typeof arg === 'string')  {
            
            this[arg] = arg.toLowerCase().replace('_', ' ');
            
        } else if (typeof arg === 'object') {
            
            for (var property in arg) {
                this[property] = arg[property];
            }
            
        } else {
            
            throw 'Invalid argument: ' + arg;
        }
    }

    Object.freeze(this);
}

/**
 * Location enum for inserts.
 **/
var Location = new Enumeration('BEF', 'AF');

/**
 * Log level for loggers.
 **/
var Log = new Enumeration('INFO','WARN','ERROR');
