package com.fbksoft.flug;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compress {

	public static void main(String[] args) throws Exception {

		String xml = "<PRM><Id_PRM>99100000971000</Id_PRM><Donnees_Releve><Id_Releve>032410</Id_Releve><Date_Releve>2014-01-06T09:30:00Z</Date_Releve><Ref_Situation_Contractuelle>872</Ref_Situation_Contractuelle><Num_Sequence>1</Num_Sequence><Id_Calendrier_Distributeur>DI0000002</Id_Calendrier_Distributeur><Libelle_Calendrier_Distributeur>GRD HP/HC</Libelle_Calendrier_Distributeur><Id_Calendrier_Fournisseur>FC000032</Id_Calendrier_Fournisseur><Libelle_Calendrier_Fournisseur>EDF HPHC</Libelle_Calendrier_Fournisseur><Type_Client>1</Type_Client><Statut_Releve>INITIAL</Statut_Releve><Motif_Releve>MES</Motif_Releve><Nature_Index>REEL</Nature_Index><Id_Affaire>0000AAAA</Id_Affaire><Ref_Fournisseur>1111111</Ref_Fournisseur><Ref_Regroupement_Fournisseur>2222</Ref_Regroupement_Fournisseur><Date_Theorique_Prochaine_Releve>2014-02-04</Date_Theorique_Prochaine_Releve><Classe_Temporelle_Distributeur><Id_Classe_Temporelle>HC</Id_Classe_Temporelle><Libelle_Classe_Temporelle>Heures Creuses</Libelle_Classe_Temporelle><Rang_Cadran>1</Rang_Cadran><Classe_Mesure>1</Classe_Mesure><Unite_Mesure>kWh</Unite_Mesure><Sens_Mesure>0</Sens_Mesure><Valeur>1200</Valeur><Nb_Chiffres_Cadran>6</Nb_Chiffres_Cadran><Indicateur_Passage_A_Zero>0</Indicateur_Passage_A_Zero><Coefficient_Lecture>1</Coefficient_Lecture><Num_Serie>22222222291201</Num_Serie></Classe_Temporelle_Distributeur><Classe_Temporelle_Distributeur><Id_Classe_Temporelle>HP</Id_Classe_Temporelle><Libelle_Classe_Temporelle>Heures Pleines</Libelle_Classe_Temporelle><Rang_Cadran>2</Rang_Cadran><Classe_Mesure>1</Classe_Mesure><Unite_Mesure>kWh</Unite_Mesure><Sens_Mesure>0</Sens_Mesure><Valeur>800</Valeur><Nb_Chiffres_Cadran>6</Nb_Chiffres_Cadran><Indicateur_Passage_A_Zero>0</Indicateur_Passage_A_Zero><Coefficient_Lecture>1</Coefficient_Lecture><Num_Serie>22222222291201</Num_Serie></Classe_Temporelle_Distributeur><Classe_Temporelle><Id_Classe_Temporelle>HC</Id_Classe_Temporelle><Libelle_Classe_Temporelle>Heures Creuses</Libelle_Classe_Temporelle><Rang_Cadran>1</Rang_Cadran><Classe_Mesure>1</Classe_Mesure><Unite_Mesure>kWh</Unite_Mesure><Sens_Mesure>0</Sens_Mesure><Valeur>900</Valeur><Nb_Chiffres_Cadran>6</Nb_Chiffres_Cadran><Indicateur_Passage_A_Zero>0</Indicateur_Passage_A_Zero><Coefficient_Lecture>1</Coefficient_Lecture><Num_Serie>22222222291201</Num_Serie></Classe_Temporelle><Classe_Temporelle><Id_Classe_Temporelle>HP</Id_Classe_Temporelle><Libelle_Classe_Temporelle>Heures Pleines</Libelle_Classe_Temporelle><Rang_Cadran>2</Rang_Cadran><Classe_Mesure>1</Classe_Mesure><Unite_Mesure>kWh</Unite_Mesure><Sens_Mesure>0</Sens_Mesure><Valeur>700</Valeur><Nb_Chiffres_Cadran>6</Nb_Chiffres_Cadran><Indicateur_Passage_A_Zero>0</Indicateur_Passage_A_Zero><Coefficient_Lecture>1</Coefficient_Lecture><Num_Serie>22222222291201</Num_Serie></Classe_Temporelle></Donnees_Releve></PRM>";

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream os = new GZIPOutputStream(out, true);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		bos.write(xml.getBytes());
		bos.close();

		byte[] byteArray = out.toByteArray();

		System.out.println("before : " + xml.getBytes().length + " after " + byteArray.length + " diff "
						+ (100 - (byteArray.length * 100 / (float) xml.getBytes().length)) + "%");

		GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(byteArray));
		BufferedReader br = new BufferedReader(new InputStreamReader(gzipInputStream));
		System.out.println(br.readLine());

	}
}
