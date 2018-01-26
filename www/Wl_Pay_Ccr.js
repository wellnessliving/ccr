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
 * @param {Object} o_event Request event object.
 */
Wl_Pay_Ccr.messageGet=function(o_event)
{
  if(o_event.origin !== 'file://')
  {
    console.log('Wl_Pay_Ccr.messageGet origin='+o_event.origin);
    return;
  }

  try
  {
    var a_data = JSON.parse(o_event.data);
  }
  catch(e)
  {
    // JSON parse error may occur in a case messages are sent from unknown sites.
    return;
  }

  if(!a_data)
    return;

  if(!a_data.hasOwnProperty('s_source')||a_data['s_source']!=='Wl_Pay_Cordova_Ccr.frame')
    return;

  if(!a_data.hasOwnProperty('i_call'))
  {
    Wl_Pay_Ccr.log({
      'a_data': a_data,
      's_message': 'Could not process request. i_call not passed.'
    });
    return;
  }

  if(!a_data.hasOwnProperty('a_argument')||(typeof a_data['a_argument']!=='object')||!(a_data['a_argument'] instanceof Array)||!a_data.hasOwnProperty('s_method'))
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

  cordova.exec(
    function(x_result)
    {
      Wl_Pay_Ccr.log({
        's_message': 'Test writing log on success.',
        'x_result': x_result
      });

      Communication.postMessage({
        'i_call': a_data.i_call,
        'is_ok': true,
        's_source': 'Wl_Pay_Ccr.top',
        'x_result': x_result
      });
    },
    function(x_result)
    {
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
    a_data['s_method'],
    a_data['a_argument']
  );
};

/**
 * Initializes credit card reader plugin.
 *
 * @protected
 */
Wl_Pay_Ccr.startup=function()
{
  if(window.addEventListener)
    window.addEventListener('message',Wl_Pay_Ccr.messageGet,false);
  else
    window.attachEvent('onmessage',Wl_Pay_Ccr.messageGet);
};
