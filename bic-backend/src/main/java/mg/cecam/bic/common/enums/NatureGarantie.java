// mg/cecam/bic/common/enums/NatureGarantie.java  — NOUVEAU
package mg.cecam.bic.common.enums;

/**
 * REELLE      : nantissement, hypothèque, gage — porte sur un bien.
 * PERSONNELLE : caution, aval — porte sur la signature d'un tiers.
 * Seules les PERSONNELLE alimentent « Total Garantie par Signature ».
 */
public enum NatureGarantie {
    REELLE,
    PERSONNELLE
}