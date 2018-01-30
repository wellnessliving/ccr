/**
 * Interface for credit card reader plugin.
 *
 * @constructor
 */
function Wl_Pay_Ccr()
{
}

/**
 * Writes messages to error log.
 *
 * @param {*[]} a_log A list of messages to write.
 */
Wl_Pay_Ccr.log=function(a_log)
{
  if(typeof a_log==='object'&&a_log instanceof Array&&!a_log.length)
    return;

  Communication.postMessage({
    'a_log': a_log,
    's_source': 'Wl_Pay_Ccr.top'
  });
};

/**
 * Processes requests to credit card reader plugin.
 *
 * @private
 * @param {{}} a_data Data arrived in the message.
 */
Wl_Pay_Ccr.messageGet=function(a_data)
{
  if(!a_data.hasOwnProperty('i_call'))
  {
    Wl_Pay_Ccr.log([{
      'a_data': a_data,
      's_message': 'Could not process request. i_call not passed.'
    }]);
    return;
  }

  if(!a_data.hasOwnProperty('a_argument')||(typeof a_data['a_argument']!=='object')||!(a_data['a_argument'] instanceof Array)||!a_data.hasOwnProperty('s_command'))
  {
    Wl_Pay_Ccr.log([{
      'a_data': a_data,
      's_message': 'Can not initiate request. Argument of method is not passed.'
    }]);

    Communication.postMessage({
      'i_call': a_data.i_call,
      'is_ok': false,
      's_source': 'Wl_Pay_Ccr.top',
      'x_result': undefined
    });
    return;
  }

  var has_result=false;

  cordova.exec(
    function(x_result)
    {
      if(typeof x_result==='object'&&x_result.hasOwnProperty('a_log'))
      {
        Wl_Pay_Ccr.log(x_result.a_log);
        delete x_result.a_log;
      }

      if(has_result)
      {
        if(typeof x_result!=='object'||!x_result.hasOwnProperty('event'))
        {
          Wl_Pay_Ccr.log([{
            'a_data': a_data,
            's_message': 'Unexpected duplicate result returned. Ignored.',
            'x_result': x_result
          }]);
          return;
        }

        Communication.postMessage({
          'a_argument': x_result,
          'event': x_result['event'],
          's_source': 'Wl_Pay_Ccr.top'
        });
      }
      else
      {
        has_result=true;

        Communication.postMessage({
          'i_call': a_data.i_call,
          'is_ok': true,
          's_source': 'Wl_Pay_Ccr.top',
          'x_result': x_result
        });
      }
    },
    function(x_result)
    {
      if(typeof x_result==='object'&&x_result.hasOwnProperty('a_log'))
      {
        Wl_Pay_Ccr.log(x_result.a_log);
        delete x_result.a_log;
      }

      has_result=true;

      Communication.postMessage({
        'i_call': a_data.i_call,
        'is_ok': false,
        's_source': 'Wl_Pay_Ccr.top',
        'x_result': x_result
      });
    },
    "Wl_Pay_Ccr",
    a_data['s_command'],
    a_data['a_argument']
  );
};

module.exports = Wl_Pay_Ccr;
