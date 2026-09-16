// mg/cecam/bic/common/enums/MotifCloture.java  — NOUVEAU
package mg.cecam.bic.common.enums;

/**
 * Distingue les deux issues d'un contrat clos, comme le fait CRIF.
 * Un remboursement anticipé et un remboursement au terme n'ont pas
 * la même lecture en risque.
 */
public enum MotifCloture {
    FIN_A_TERME,
    FIN_ANTICIPEE
}