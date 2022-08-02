laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class GroupingUtils {

	
	/**
	 * Finds all the SuperCandidates out of the PatternCandidates already detected.
	 * This is the first step of the grouping procedure.
	 */
	static findSuperCandidates(pattern, candidates) {
		let superCandidates = [];
		for (var c1 of candidates) {
			for (var c2 of candidates) {
				if (c1 != c2) {
					var count = 0;
					var dissimilarity = 0;
					for (var i = 0; i < pattern.members.length; i++) {
						if (c1[i] == c2[i])
							count++;
						else
							dissimilarity = i;
					}
					if (count != (pattern.members.length - 1)) continue;
					
					// Checking for duplicates
					var flag = true;
					var flag2 = true;
					if (superCandidates.length != 0) {
						for (var s of superCandidates) {
							if (s.mergers.includes(c1) && s.mergers.includes(c2))
								flag = false;
						}

					}
					if (flag) {
						// Create new SuperCandidate or fill an existing one
						if (superCandidates.length != 0) {
							for (var s of superCandidates) {
								if (s.dissimilarity == dissimilarity
										&& (s.mergers.includes(c1) || s.mergers.includes(c2))) {
									// Fill an existing SuperCandidate
									if (s.mergers.includes(c1)) {
										s.addDissimilarMember(c2);
										flag2 = false;
									} else {
										s.addDissimilarMember(c1);
										flag2 = false;
									}
								}
							}
						}
						if (flag2) {
							// Create new SuperCandidate
							let temp = new SuperCandidate(c1, dissimilarity);
							temp.addDissimilarMember(c2);
							superCandidates.push(temp);
						}

					}
				}
			}
		}
		
		let allSuperCandidates = [... superCandidates];
		// Now check for Candidates left behind without a merge and add them in the SuperCandidates ArrayList
		for (var c of candidates) {
			var flag = false;
			for (var s of superCandidates) {
				if (s.mergers.includes(c)) {
					flag = true;
					break;
				}
			}
			// if false, that means this candidate is not part of a SuperCandidate
			if (flag == false) {
				allSuperCandidates.push(c);
			}

		}
		
		return allSuperCandidates;
	}
	 

	/**
	 * Finds all the HyperCandidates out of the SuperCandidates and PatternCandidates already detected.
	 * This is the final step of the grouping procedure.
	 */
	static findHyperCandidates(pattern, superCandidates) {
		let hyperCandidates = [];
		let dissimilarity = [];
		for (var c1 of superCandidates) {
			for (var c2 of superCandidates) {
				if (c1 != c2) {
					dissimilarity = [];
					if (c1 instanceof SuperCandidate && c2 instanceof SuperCandidate) {
						for (var i = 0; i < pattern.members.length; i++) {
							if (c1.getDissimilarity() != i && c2.getDissimilarity() != i) {
								if (c1.members[i] != c2.members[i])
									dissimilarity.push(i);
							} else {
								dissimilarity.push(c1.getDissimilarity());
								if (c1.getDissimilarity() != c2.getDissimilarity())
									dissimilarity.push(c2.getDissimilarity());
							}
						}
					} else if (c1 instanceof SuperCandidate) {
						for (var i = 0; i < pattern.members.length; i++) {
							if (c1.getDissimilarity() != i) {
								if (c1.members[i] != c2[i])
									dissimilarity.push(i);
							} else {
								dissimilarity.push(c1.getDissimilarity());
							}
						}
					} else if (c2 instanceof SuperCandidate) {
						for (var i = 0; i < pattern.members.length; i++) {
							if (c2.getDissimilarity() != i) {
								if (c1[i] != c2.members[i])
									dissimilarity.push(i);
							} else {
								dissimilarity.push(c2.getDissimilarity());
							}
						}
					} else {
						// Neither c1 nor c2 are SuperCandidates
						for (var i = 0; i < pattern.members.length; i++) {
							if (c1[i] != c2[i])
								dissimilarity.push(i);
						}
					}
					if (dissimilarity.length != 2) continue;
					
					// Checking for duplicates
					var flag = true;
					var flag2 = true;
					if (hyperCandidates.length != 0) {
						for (var s of hyperCandidates) {
							if (s.members.includes(c1) && s.mergers.includes(c2))
								flag = false;
						}
					}
					if (flag) {
						// Create new HyperCandidate or fill an existing one
						if (hyperCandidates.length != 0) {
							for (var h of hyperCandidates) {
								if (h.getDissimilarity1() == dissimilarity[0]
										&& h.getDissimilarity2() == dissimilarity[1]
										&& (h.mergers.includes(c1) || h.mergers.includes(c2))) {
									// Fill an existing SuperCandidate
									if (h.mergers.includes(c1)) {
										h.addHyperMember(c2);
										flag2 = false;
									} else {
										h.addHyperMember(c1);
										flag2 = false;
									}
								}
							}
						}
						if (flag2) {
							// Create new HyperCandidate
							var temp = new HyperCandidate(c1, dissimilarity);
							temp.addHyperMember(c2);
							hyperCandidates.push(temp);
						}
					}
				}
			}
		}
		
		var allHyperCandidates = [... hyperCandidates];
		// Now check for Candidates left behind without a merge and add them in the TotalHyperCandidates ArrayList
		for (var c of superCandidates) {
			var flag = false;
			for (var s of hyperCandidates) {
				if (s.mergers.includes(c)) {
					flag = true;
					break;
				}
			}
			// if false, that means this candidate is not part of a HyperCandidate
			if (flag == false) {
				allHyperCandidates.push(c);
			}

		}
		
		return allHyperCandidates;
	}
}

/**
 * Represents Several PatternCandidates grouped up under a single dissimilarity. A SuperCandidate consists of 2 or more
 * PatternCandidates with 1 specific Ability being dissimilar.
 */
class SuperCandidate {

	constructor(c, d) {
		this.dissimilarity = d;
		
		this.members = [];
		this.mergedMembers = [];
		this.mergers = [];
		
		for (var i = 0; i < c.length; i++) {
			
			this.members.push(c[i]);
			// MemberAbilities.add(c.getMemberAbilities().get(i));
			// MemberNames.add(c.getMemberNames().get(i));
			if (i == d) {
				this.mergedMembers.push(c[i]);
			}
		}
		this.mergers.push(c);
	}

	addDissimilarMember(c) {
		this.mergers.push(c);
		this.mergedMembers.push(c[this.dissimilarity]);
	}
	
	getDissimilarity() {
		return this.dissimilarity;
	}

	setDissimilarity(dissimilarity) {
		this.dissimilarity = dissimilarity;
	}
}

class HyperCandidate {


	constructor(c, d) {
		this.dissimilarity1 = d[0];
		this.dissimilarity2 = d[1];
		
		this.members = [];
		this.mergers = [];
		this.Diss1Members = [];
		this.Diss2Members = [];
		for (var i = 0; i < c.length; i++) {
		
			if (Array.isArray(c)) {
				this.members.push(c[i]);
			}
			else {
				this.members.push(c.members[i]);
			}
			// MemberAbilities.add(c.getMemberAbilities().get(i));
			// MemberNames.add(c.getMemberNames().get(i));
			
			if (i != this.dissimilarity1 && i != this.dissimilarity2) {

			} else {
				
				if (c instanceof SuperCandidate) {
					if (i == this.dissimilarity1) {
						if (c.getDissimilarity() == i) {
							this.Diss1Members.push(c.mergedMembers);
						} else {
							var temp = [];
							temp.push(c.members[i]);
							this.Diss1Members.push(temp);
						}
					} else {
						if (c.getDissimilarity() == i) {
							this.Diss2Members.push(c.mergedMembers);
						} else {
							var temp = [];
							temp.push(c.members[i]);
							this.Diss2Members.push(temp);
						}
					}
				} else {
					var temp = [];
					temp.push(c[i]);
					this.Diss1Members.push(temp);
					if (i == this.dissimilarity1) {
						this.Diss1Members.push(temp);
					} else {
						this.Diss2Members.push(temp);
					}
				}
			}
		}
		this.mergers.push(c);
	}

	/**
	 * Adds a PatternCandidate or SuperCandidate to this HyperCandidate.
	 * 
	 * @param c the PatternCandidate or SuperCandidate to be added.
	 */
	addHyperMember(c) {
		this.mergers.push(c);
		if (c instanceof SuperCandidate) {
			if (c.getDissimilarity() == this.dissimilarity1) {
				this.Diss1Members.push(c.mergedMembers);
			} else {
				var temp = [];
				temp.push(c.members[this.dissimilarity1]);
				this.Diss1Members.push(temp);
			}
			if (c.getDissimilarity() == this.dissimilarity2) {
				this.Diss2Members.push(c.mergedMembers);
			} else {
				var temp = [];
				temp.push(c.members[this.dissimilarity2]);
				this.Diss2Members.push(temp);
			}
		} else {
			var temp = [];
			temp.push(c[this.dissimilarity1]);
			this.Diss1Members.push(temp);

			var temp2 = [];
			temp2.push(c[this.dissimilarity2]);
			this.Diss2Members.push(temp2);
		}
	}

	getDissimilarity1() {
		return this.dissimilarity1;
	}

	getDissimilarity2() {
		return this.dissimilarity2;
	}

}
