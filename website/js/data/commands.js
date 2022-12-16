const commands = [
  {
    name: 'abs',
    type: 'Number',
    parameters: [
      {
        name: 'value',
        optional: false,
        type: 'Number'
      }
    ],
    description: 'Returns the absolute value of the given number.'
  },
  {
    name: 'acos',
    type: 'Number',
    parameters: [
      {
        name: 'value',
        optional: false,
        type: 'Number'
      }
    ],
    description: `Returns the angle in degrees corresponding to the given number.<br>
                        The inputs may range from -1.0 to 1.0 and the outputs will range from 0 to 180 degrees.<br>
                        A few examples of inputs and their corresponding outputs:<br>
                        <table>
                        <tr><th>INPUT</th><th>OUTPUT</th></tr>
                        <tr><th>1</th><th>0 degrees</th></tr>
                        <tr><th>0.5</th><th>60 degrees</th></tr>
                        <tr><th>0</th><th>90 degrees</th></tr>
                        <tr><th>-1</th><th>180 degrees</th></tr>
                        </table>`
  },
  {
    name: 'addEffect',
    type: 'Effect',
    parameters: [
      {
        name: 'target',
        type: 'Instance',
      },
      {
        name: 'effect',
        type: 'Effect',
      }
    ],
    description: `Adds an effect of the specified type to the given instance and returns the effect.`
  },
  {
    name: 'addEffect',
    type: 'Effect',
    parameters: [
      {
        name: 'target',
        type: 'Instance',
      },
      {
        name: 'name',
        type: 'String',
        description: `Name of the effect`,
      },
      {
        name: 'duration',
        type: 'Number',
        description: `How many ticks the effect should last on the instance`,
      },
      {
        name: 'attributes',
        type: 'List',
        description: `The list of attributes that should be attached to the effect`
      }
    ],
    description: `Adds an effect to the given instance and returns the effect.<br>
                        The effect is uniquely created by this command.<br>
                        You can define the name of the effect and for how long it should last on the given instance.<br>
                        Additionally, you can define a list of attributes that should be added to the effect.`,
    example: `addEffect(self, "Well Rested", 12, ["attHealth", 5, "attStress", -10]);<br>
                        <i>// Adds an effect to self for 12 ticks that adds 5 to health and reduces stress by 10</i>`,
  },
  {
    name: 'addPersonalAtt',
    type: 'Number',
    parameters: [
      {
        name: 'target',
        type: 'Instance'
      },
      {
        name: 'attribute',
        type: 'String',
        description: `The text ID of the attribute (eg. 'attHealth')`
      },
      {
        name: 'amount',
        type: 'Number'
      }
    ],
    description: `Adds the specified amount to the attribute value with the given text ID to the target instance.`,
    example: `addPersonalAtt(self, "attHealth", 7);<br>
                         <i>// Adds 7 to the health attribute of self</i>`,
  },
  {
    name: 'addOccupation',
    type: 'Void',
    parameters: [
      {
        name: 'target',
        type: 'Instance'
      },
      {
        name: 'duration',
        type: 'Number',
        description: `The duration of the occupation. Is rounded down to next integer`
      },
      {
        name: 'callback script',
        type: 'Script',
        optional: true
      }
    ],
    description: `Occupies the target instance for the given number of ticks. During this duration, the instance
                         does not execute processes caused by its drives.<br>
                         If a callback script was given, it is executed after the occupation has expired.`,
    example: `addOccupation(victim, 12);<br>
                         <i>// Prevents the victim from tending to its drives for 12 ticks</i><br>
                         <br>
                         addOccupation(self, 100, ->awake);<br>
                         <i>// Occupies self for 100 ticks, after which the 'awake' script is executed</i>`,
  },
  {
    name: 'ceil',
    type: 'Number',
    parameters: [
      {
        name: 'value',
        type: 'Number'
      }
    ],
    description: `The given number is rounded up to the next integer.`
  },
  {
    name: 'chance',
    type: 'Number',
    parameters: [
      {
        name: 'probability',
        type: 'Number'
      }
    ],
    description: `Returns 1 with the given probability p, or 0 with the probability (1 - p). Thus, if you call this
                      command many times with the value 0.3, then ~30% of the returned values will be true
                      (1) and the remainder of ~70% will be false (0).<br>
                      Values below 0 will always return false and values of 1 or higher will always be true.`,
    example: `chance(0.8);<br>
                      <i>// will return 1 with a 80% probability and 0 with a 20% probability</i>`
  },
  {
    name: 'change',
    type: 'Instance',
    parameters: [
      {
        name: 'target',
        type: 'Instance'
      },
      {
        name: 'container',
        type: 'Container'
      }
    ],
    description: `Changes the type of the target instance to the given container. Inherits the attributes of its
                         new container.<br>
                         Returns the target instance.`,
    example: `tree = change(sapling, entTree);<br>
                         <i>// changes the sapling instance into a tree</i>`
  },
  {
    name: 'moveTo',
    type: 'Tile',
    parameters: [
      {
        name: 'target',
        type: 'Tile'
      },
      {
        name: 'instance',
        type: 'Instance'
      },
      {
        name: 'steps',
        type: 'Number'
      },
      {
        name: 'viewingDistance',
        type: 'Number',
        optional: true,
      }
    ],
    description: `The instance takes the specified number of steps towards the target tile. The path that the
                         instance chooses is informed by the viewingDistance.<br>
                         Returns the tile where the instance lands.`,
  },
  {
    name: 'random',
    type: 'Number',
    parameters: [
      {
        name: 'lower',
        optional: true,
        type: 'Number',
        description: 'lower bound'
      },
      {
        name: 'upper / list',
        optional: false,
        type: 'Number / List',
        description: 'upper bound or list'
      }
    ],
    description: `Returns a random integer number that ranges from the lower bound (default 0) to the upper bound.<br>
                        More specifically, the range is [lower, upper), which means that the upper bound is never returned.<br>
                        If only one parameter is given and it has the type List, then a random element from the list is returned.`,
  },
];
