if(tools == undefined)
	var tools = new Object();

tools.xilinx = {

	init: function(args){
		with(this){
			if(args == undefined){
				if(	 tools.xilinx.ModuleName == undefined
				   ||
				     tools.xilinx.pathFolder == undefined
				   ||
				   	 org.reflect.xilinx.XilinxDefs.pathISETools == undefined
				   ||
				     tools.xilinx.rt == undefined
				   )
					 throw("Undefined parameters for xilinx tool");
			     else return;
			} 
			if(args["ModuleName"] == undefined){
				if(tools.xilinx.ModuleName == undefined)
					throw("No ModuleName defined for running the tool!");
				else println("Using ModuleName: "+tools.xilinx.ModuleName+" as default.");
			}else tools.xilinx.ModuleName = args["ModuleName"];
			
			if(args["pathFolder"] == undefined){
				if(tools.xilinx.pathFolder == undefined)
					throw("No pathFolder defined for running the tool!");
				else println("Using pathFolder: "+tools.xilinx.pathFolder+" as default.");
			}else tools.xilinx.pathFolder = args["pathFolder"];
			
			if(args["pathISETools"] == undefined){
				if(org.reflect.xilinx.XilinxDefs.pathISETools == null)
					throw("No pathISETools defined for running the tool!");
				else println("Using pathISETools: "+org.reflect.xilinx.XilinxDefs.pathISETools+" as default.");
			}else org.reflect.xilinx.XilinxDefs.pathISETools = args["pathISETools"];
			
			if(tools.xilinx.rt == undefined)
				tools.xilinx.rt = new org.reflect.xilinx.RunTools(pathFolder);
		}
	},

	xst: function(args){
		
		
		with(tools.xilinx){
			init(args);
			tools.xilinx.xstOptions = new java.util.HashMap();
			xstOptions.put("-ifmt", "mixed");
			xstOptions.put("-ofmt", "NGC");
			xstOptions.put("-p", "xc5vlx50t-2-ff1136");
			xstOptions.put("-opt_mode", "Speed");
			xstOptions.put("-opt_level", "1");
			xstOptions.put("-power", "NO");
			xstOptions.put("-iuc", "NO");
			xstOptions.put("-keep_hierarchy", "NO");
			xstOptions.put("-netlist_hierarchy", "As_Optimized");
			xstOptions.put("-rtlview", "YES");
			xstOptions.put("-glob_opt", "AllClockNets");
			xstOptions.put("-read_cores", "YES");
			xstOptions.put("-write_timing_constraints", "NO");
			xstOptions.put("-cross_clock_analysis", "NO");
			xstOptions.put("-hierarchy_separator", "/");
			xstOptions.put("-bus_delimiter", "<>");
			xstOptions.put("-case", "Maintain");
			xstOptions.put("-slice_utilization_ratio", "100");
			xstOptions.put("-bram_utilization_ratio", "100");
			xstOptions.put("-dsp_utilization_ratio", "100");
			xstOptions.put("-lc", "Off");
			xstOptions.put("-reduce_control_sets", "Off");
			xstOptions.put("-verilog2001", "YES");
			xstOptions.put("-fsm_extract", "YES");
			xstOptions.put("-fsm_encoding", "Auto");
			xstOptions.put("-safe_implementation", "No");
			xstOptions.put("-fsm_style", "LUT");
			xstOptions.put("-ram_extract", "Yes");
			xstOptions.put("-ram_style", "Auto");
			xstOptions.put("-rom_extract", "Yes");
			xstOptions.put("-mux_style", "Auto");
			xstOptions.put("-decoder_extract", "YES");
			xstOptions.put("-priority_extract", "YES");
			xstOptions.put("-shreg_extract", "YES");
			xstOptions.put("-shift_extract", "YES");
			xstOptions.put("-xor_collapse", "YES");
			xstOptions.put("-rom_style", "Auto");
			xstOptions.put("-auto_bram_packing", "NO");
			xstOptions.put("-mux_extract", "YES");
			xstOptions.put("-resource_sharing", "YES");
			xstOptions.put("-async_to_sync", "NO");
			xstOptions.put("-use_dsp48", "Auto");
			xstOptions.put("-iobuf", "YES");
			xstOptions.put("-max_fanout", "100000");
			xstOptions.put("-bufg", "32");
			xstOptions.put("-register_duplication", "YES");
			xstOptions.put("-register_balancing", "NO");
			xstOptions.put("-slice_packing", "YES");
			xstOptions.put("-optimize_primitives", "NO");
			xstOptions.put("-use_clock_enable", "Auto");
			xstOptions.put("-use_sync_set", "Auto");
			xstOptions.put("-use_sync_reset", "Auto");
			xstOptions.put("-iob", "Auto");
			xstOptions.put("-equivalent_register_removal", "YES");
			xstOptions.put("-slice_utilization_ratio_maxmargin", "5");
			
			if(args.xstOptions != undefined)
				for(var __opt__ in args.xstOptions)
					xstOptions.put(__opt__, args.xstOptions[__opt__]);
			return "attributes.set( {function: {f1: { cost: 10}}});"; 
			rt.setXstOptions(xstOptions);
			rt.synth(ModuleName);
			
			
			(new org.reflect.xilinx.Report()).getXSTReportStr(rt.workingFolderStr,"function", ModuleName);
			return 'attributes.set({'+
								'function:{'+
										'MajorityVoter:{'+
											'msgWarnings:"0",'+
											'device:"5vlx50tff1136",'+
											'msgInfos:"0",'+
											'delay:"20.167",'+
											'numSliceLUTs:"597",'+
											'msgErrors:"0",'+
										'}'+
								'}'+
						'});';
			}
		/***/ 
			
	},

	xpr: function(args){
		with(tools.xilinx){
			init(args);
			rt.xpr(ModuleName);
			return (new org.reflect.xilinx.Report()).getXPRReportStr(
	                      rt.workingFolderStr, "function", ModuleName);
		}
	},

	ngdbuild: function(args){
		with(tools.xilinx){
			init(args);
			rt.buildNgd(ModuleName, null); // no ucFile
		}
	},
 
	map: function(args){
		with(tools.xilinx){
			init(args);
			 rt.map(ModuleName);
			 
			 return (new org.reflect.xilinx.Report()).getMAPReportStr(
					 	rt.workingFolderStr, "function", ModuleName);
		}
	},

	par: function(args){
	with(tools.xilinx){
		init(args);
		rt.par(ModuleName);
        return (new org.reflect.xilinx.Report()).getPARReportStr(
        			rt.workingFolderStr, "function", ModuleName);
	}
	}
}