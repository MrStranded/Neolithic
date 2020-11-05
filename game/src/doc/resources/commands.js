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
        description:   `Adds an effect to the given instance and returns the effect.<br>
                        The effect is uniquely created by this command.<br>
                        You can define the name of the effect and for how long it should last on the given instance.<br>
                        Additionally, you can define a list of attributes that should be added to the effect.`,
        example:       `addEffect(self, "Well Rested", 12, ["attHealth", 5, "attStress", -10]);<br>
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
        description:    `Adds the specified amount to the attribute value with the given text ID to the target instance.`,
        example:        `addPersonalAtt(self, "attHealth", 7);<br>
                         <i>// Adds 7 to the health attribute of self</i>`,
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
        description:   `Returns a random integer number that ranges from the lower bound (default 0) to the upper bound.<br>
                        More specifically, the range is [lower, upper), which means that the upper bound is never returned.<br>
                        If only one parameter is given and it has the type List, then a random element from the list is returned.`,
    },
];