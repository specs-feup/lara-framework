/*
function libToObj(str){
	return { unar_op: [
			{
				operations: ["@cast"],
				operand: [
							{
								"$role": "@input",
								instance: [ 
											{
												"$operation": "@cast",
												"$type": "@realt",
												"$size": 64,
												"$port": "input"
											}
										   ]
							},
							{
								"$role":"@result",
								instance: [
											{
												"$operation": "@cast",
												"$type": "@intt",
												"$size": 64,
												"$port": "output"
											}
										  ]
							}
				],
				"$frequency": 142,
				"$cycles": 9,
				"$pipeline_interval": 1,
				"$library_name": "work",
				"$component_name": "fp_dp_longlong_signed_top",
				"$reset": "rst",
				"$clock": "clk"
			},
			{
				operations: ["@cast"],
				operand: [
							{
								"$role": "@input",
								instance: [ 
											{
												"$operation": "@cast",
												"$type": "@realt",
												"$size": 32,
												"$port": "input"
											}
										   ]
							},
							{
								"$role":"@result",
								instance: [
											{
												"$operation": "@cast",
												"$type": "@intt",
												"$size": 64,
												"$port": "output"
											}
										  ]
							}
				],
				"$frequency": 142,
				"$cycles": 9,
				"$pipeline_interval": 1,
				"$library_name": "work",
				"$component_name": "fp_sp_longlong_signed_top",
				"$reset": "rst",
				"$clock": "clk"
			}
		],
		bin_op: [ 
			{
				operations: ["@le"],
				operand: [
							{
								"$role": "@left",
								instance: [ 
											{
												"$operation": "@le",
												"$type": "@realt",
												"$size": 32,
												"$port": "a"
											}
										   ]
							},
							{
								"$role": "@right",
								instance: [ 
											{
												"$operation": "@le",
												"$type": "@realt",
												"$size": 32,
												"$port": "b"
											}
										   ]
							},
							{
								"$role":"@result",
								instance: [
											{
												"$operation": "@le",
												"$type": "@intt",
												"$size": 1,
												"$port": "result"
											}
										  ]
							}
				],
				"$frequency": 341,
				"$cycles": 2,
				"$pipeline_interval": 1,
				"$library_name": "work",
				"$component_name": "fp_sp_less_equal_top",
				"$clock": "clk"
			},
			{
				operations: ["@ge"],
				operand: [
							{
								"$role": "@left",
								instance: [ 
											{
												"$operation": "@ge",
												"$type": "@realt",
												"$size": 32,
												"$port": "a"
											}
										   ]
							 },
							 {
								"$role": "@right",
								instance: [ 
											{
												"$operation": "@ge",
												"$type": "@realt",
												"$size": 32,
												"$port": "b"
											}
										   ]
							},
							{
								"$role":"@result",
								instance: [
											{
												"$operation": "@gt",
												"$type": "@intt",
												"$size": 1,
												"$port": "result"
											}
										  ]
							}
				],
				"$frequency": 341,
				"$cycles": 2,
				"$pipeline_interval": 1,
				"$library_name": "work",
				"$component_name": "fp_sp_large_equal_top",
				"$clock": "clk"
			}
		],
		toLib: objToLib(this,"","")
	};
	/**
}
*/



function objToLib(obj, space, prefix){
	var out = "";
	if(space == undefined)
		space = "";
	if(obj.constructor==Array){
		if(obj[0].constructor==Object || obj[0].constructor==Array)
			for(var prop in obj){
				out+= space+"#start_"+prefix+"\n";
					out+=objToLib(obj[prop], space+"\t");
				out+= space+"#end_"+prefix+"\n";
			}
		else{
			out+= space+"#start_"+prefix+"\n";
				out+= space+"\t"+obj[0];
				for(var i = 1; i < obj.length; i++){
					out+= ","+obj[i];
				}
			out+= "\n"+space+"#end_"+prefix+"\n";
		}
	}else
	for(var prop in obj){
		if ( obj[prop].constructor==Array)
			out+=objToLib(obj[prop], space,prop);
		else
		if ( obj[prop].constructor==Object){
			out+= space+"#start_"+prop+"\n";
				out+=objToLib(obj[prop], space+"\t");
			out+= space+"#end_"+prop+"\n";
		}else 
			out+= space+prop+"="+obj[prop]+"\n";
	}
	return out;
}