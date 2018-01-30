/**
 * Interface for credit card reader plugin.
 *
 * @constructor
 */
function Wl_Pay_Ccr()
{
}

/**
 * Writes a message to error log.
 *
 * @param {String} s_message Text of the message to write.
 */
Wl_Pay_Ccr.log=function(s_message)
{
  Communication.postMessage({
    'log': s_message,
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
    Wl_Pay_Ccr.log({
      'a_data': a_data,
      's_message': 'Could not process request. i_call not passed.'
    });
    return;
  }

  if(!a_data.hasOwnProperty('a_argument')||(typeof a_data['a_argument']!=='object')||!(a_data['a_argument'] instanceof Array)||!a_data.hasOwnProperty('s_command'))
  {
    Wl_Pay_Ccr.log({
      'a_data': a_data,
      's_message': 'Can not initiate request. Argument of method is not passed.'
    });

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
      has_result=true;

      Wl_Pay_Ccr.log({
        's_message': 'Test writing log on success.',
        'x_result': x_result
      });

      if(has_result)
      {
        if(typeof x_result!=='object'||!x_result.hasOwnProperty('event'))
        {
          Wl_Pay_Ccr.log({
            'a_data': a_data,
            's_message': 'Unexpected duplicate result returned. Ignored.',
            'x_result': x_result
          });
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
      has_result=true;

      Wl_Pay_Ccr.log({
        's_message': 'Test writing log on error.',
        'x_result': x_result
      });

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
