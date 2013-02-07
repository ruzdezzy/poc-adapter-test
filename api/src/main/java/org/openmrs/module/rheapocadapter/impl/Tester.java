package org.openmrs.module.rheapocadapter.impl;

import java.util.List;

import org.openmrs.Encounter;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;

public class Tester {

	/**
	 * @param args
	 * @throws HL7Exception
	 * @throws EncodingNotSupportedException
	 * @throws ApplicationException
	 */
	public static void main(String[] args) {
		try {
			RHEA_ORU_R01Handler rHEA_ORU_R01Handler = new RHEA_ORU_R01Handler(
					null);
			String mess = "<?xml version=\"1.0\"?> <ORU_R01 xmlns=\"urn:hl7-org:v2xml\"> <MSH> <MSH.1>|</MSH.1> <MSH.2>^~\\&amp;</MSH.2> <MSH.6> <HD.1>Point of Care</HD.1> </MSH.6> <MSH.7> <TS.1>20121005084254</TS.1> </MSH.7> <MSH.9> <MSG.1>ORU</MSG.1> <MSG.2>R01</MSG.2> <MSG.3>ORU_R01</MSG.3> </MSH.9> <MSH.10>a70d7108-5a0c-4818-8360-f73a22f7fd65</MSH.10> <MSH.11> <PT.1>D</PT.1> <PT.2>C</PT.2> </MSH.11> <MSH.12> <VID.1>2.5</VID.1> <VID.2> <CE.1>RWA</CE.1> </VID.2> </MSH.12> <MSH.21> <EI.1>CLSM_V0.83</EI.1> </MSH.21> </MSH> <ORU_R01.PATIENT_RESULT> <ORU_R01.PATIENT> <PID> <PID.3> <CX.1>1199170003455088</CX.1> <CX.5>NID</CX.5> </PID.3> <PID.5> <XPN.1> <FN.1>ALICE</FN.1> </XPN.1> <XPN.2>MUKAMURIGO</XPN.2> </PID.5> <PID.7> <TS.1>19910430</TS.1> </PID.7> </PID> </ORU_R01.PATIENT> <ORU_R01.ORDER_OBSERVATION> <ORC> <ORC.1>RE</ORC.1> <ORC.9> <TS.1>201210050842</TS.1> </ORC.9> <ORC.12> <XCN.1>38</XCN.1> </ORC.12> <ORC.16> <CE.1>Identifier</CE.1> <CE.2>Text</CE.2> <CE.3>Name of Coding System</CE.3> </ORC.16> </ORC> <OBR> <OBR.1>0</OBR.1> <OBR.3> <EI.1>5</EI.1> </OBR.3> <OBR.4> <CE.2>ANC Physical</CE.2> </OBR.4> <OBR.7> <TS.1>201210010000</TS.1> </OBR.7> <OBR.16> <XCN.1>1198080018198077 </XCN.1> <XCN.2> <FN.1>MUSABWA</FN.1> </XCN.2> <XCN.3>JACQUES</XCN.3> <XCN.13>NID</XCN.13> </OBR.16> <OBR.20>363</OBR.20> <OBR.21>OMRS-Ruhunda</OBR.21> </OBR> </ORU_R01.ORDER_OBSERVATION> <ORU_R01.ORDER_OBSERVATION> <OBR> <OBR.1>1</OBR.1> <OBR.18>0</OBR.18> <OBR.29> <EIP.2> <EI.3>5</EI.3> </EIP.2> </OBR.29> </OBR> <ORU_R01.OBSERVATION> <OBX> <OBX.1>0</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>84862-4</CE.1> <CE.2>DIASTOLIC BLOOD PRESSURE</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>68.0</OBX.5> <OBX.6> <CE.1>mmHg</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161045</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>1</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>29463-7</CE.1> <CE.2>WEIGHT (KG)</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>50.0</OBX.5> <OBX.6> <CE.1>kg</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161047</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>2</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>11885-1</CE.1> <CE.2>NUMBER OF WEEKS PREGNANT</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>10.0</OBX.5> <OBX.6> <CE.1>weeks</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161046</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>3</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>8310-5</CE.1> <CE.2>TEMPERATURE (C)</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>37.0</OBX.5> <OBX.6> <CE.1>DEG C</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161046</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>4</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>11881-0</CE.1> <CE.2>Length of the uterus (fundal height) in cm</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>8.0</OBX.5> <OBX.6> <CE.1>cm</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161046</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>5</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>55283-6</CE.1> <CE.2>Heart rate of fetus</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>120.0</OBX.5> <OBX.6> <CE.1>BPM</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161048</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>6</OBX.1> <OBX.3> <CE.1>46040-2</CE.1> <CE.2>WEIGHT CHANGE</CE.2> <CE.3>LOINC</CE.3> </OBX.3> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>7</OBX.1> <OBX.2>NM</OBX.2> <OBX.3> <CE.1>8480-6</CE.1> <CE.2>SYSTOLIC BLOOD PRESSURE</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5>124.0</OBX.5> <OBX.6> <CE.1>mmHg</CE.1> <CE.3>ucum</CE.3> </OBX.6> <OBX.14> <TS.1>20121001161047</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> </ORU_R01.ORDER_OBSERVATION> <ORU_R01.ORDER_OBSERVATION> <OBR> <OBR.1>2</OBR.1> <OBR.3> <EI.1>18</EI.1> </OBR.3> <OBR.4> <CE.2>ANC Maternal Treatments and Interventions</CE.2> </OBR.4> <OBR.7> <TS.1>201210010000</TS.1> </OBR.7> <OBR.16> <XCN.1>b6d79622-9cfe-1031-84cf-bd846ceebe61</XCN.1> <XCN.2> <FN.1>MUSABWA</FN.1> </XCN.2> <XCN.3>JACQUES</XCN.3> <XCN.13>EPID</XCN.13> </OBR.16> <OBR.20>363</OBR.20> <OBR.21>OMRS-Ruhunda</OBR.21> </OBR> </ORU_R01.ORDER_OBSERVATION> <ORU_R01.ORDER_OBSERVATION> <OBR> <OBR.1>3</OBR.1> <OBR.18>2</OBR.18> <OBR.29> <EIP.2> <EI.3>18</EI.3> </EIP.2> </OBR.29> </OBR> <ORU_R01.OBSERVATION> <OBX> <OBX.1>0</OBX.1> <OBX.2>CE</OBX.2> <OBX.3> <CE.1>72180-3</CE.1> <CE.2>Was the woman given iron and folic acid?</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5> <CE.1>1066</CE.1> <CE.2>NO</CE.2> <CE.3>RWCS</CE.3> </OBX.5> <OBX.14> <TS.1>20121001163939</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>1</OBX.1> <OBX.2>CE</OBX.2> <OBX.3> <CE.1>72178-7</CE.1> <CE.2>Given Mosquito Nets</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5> <CE.1>1065</CE.1> <CE.2>YES</CE.2> <CE.3>RWCS</CE.3> </OBX.5> <OBX.14> <TS.1>20121001163940</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>2</OBX.1> <OBX.2>CE</OBX.2> <OBX.3> <CE.1>72179-5</CE.1> <CE.2>Given Sulfadoxin Pyrimethamine</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5> <CE.1>1066</CE.1> <CE.2>NO</CE.2> <CE.3>RWCS</CE.3> </OBX.5> <OBX.14> <TS.1>20121001163940</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>3</OBX.1> <OBX.2>CE</OBX.2> <OBX.3> <CE.1>72187-8</CE.1> <CE.2>given tetanus vaccine</CE.2> <CE.3>LOINC</CE.3> </OBX.3> <OBX.5> <CE.1>1065</CE.1> <CE.2>YES</CE.2> <CE.3>RWCS</CE.3> </OBX.5> <OBX.14> <TS.1>20121001163938</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> <ORU_R01.OBSERVATION> <OBX> <OBX.1>4</OBX.1> <OBX.2>CE</OBX.2> <OBX.3> <CE.1>8406</CE.1> <CE.2>Given Mebendazole</CE.2> <CE.3>RWCS</CE.3> </OBX.3> <OBX.5> <CE.1>1066</CE.1> <CE.2>NO</CE.2> <CE.3>RWCS</CE.3> </OBX.5> <OBX.14> <TS.1>20121001163938</TS.1> </OBX.14> </OBX> </ORU_R01.OBSERVATION> </ORU_R01.ORDER_OBSERVATION> </ORU_R01.PATIENT_RESULT> </ORU_R01>";
			// String ms = EncodedMessageComparator.standardize(mess);
			Message message = rHEA_ORU_R01Handler.changeStringToMessage(mess);

			List<Encounter> encs = rHEA_ORU_R01Handler
					.processORU_R01((ORU_R01) rHEA_ORU_R01Handler
							.processMessage(message));
			System.out.println(encs.size());
		} catch (HL7Exception e) {

			System.out.println(e.getMessage());
		} catch (ApplicationException e) {

			System.out.println(e.getMessage());
		}

	}

}
