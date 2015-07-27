package csearch.prog.fhbgds;

public enum Demand {
	NONE,
	VERY_LOW,
	LOW,
	MEDIUM,
	HUGE,
	EXTREME;
	
		private static final int iEXTREME = 300;
		private static final int iHIGH = 200;
		private static final int iMEDIUM = 125;
		private static final int iLOW = 25;
		
		public static Demand getDemandLevel(int num){
			if(num < 0) num = 0;
			if(num < iLOW) 						return VERY_LOW;
			if(num >= iLOW && num < iMEDIUM) 	return LOW;
			if(num >= iMEDIUM && num < iHIGH) 	return MEDIUM;
			if(num >= iHIGH && num < iEXTREME)	return HUGE;
			if(num >= iEXTREME)					return EXTREME;
			
			return NONE;
		}
}
