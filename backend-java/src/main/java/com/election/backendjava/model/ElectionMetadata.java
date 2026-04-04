package com.election.backendjava.model;

/**
 * Holds national-level metadata for the election, like cast, counted, rejected and uncounted votes.
 */
public class ElectionMetadata {

        private int cast;
        private int totalCounted;

        // rejected
        private int rejectedInvalid;
        private int rejectedBlank;

        // uncounted
        private int uncountedProxyCertificates;
        private int uncountedTooManyBallots;
        private int uncountedTooFewBallots;
        private int uncountedTakenAway;
        private int uncountedTooFewIssued;
        private int uncountedTooManyIssued;
        private int uncountedNoExplanation;
        private int uncountedOther;
        private int uncountedLost;
        private int uncountedTooManyMail;
        private int uncountedNoMail;

        // Getters & setters
        public int getCast() { return cast; }
        public void setCast(int cast) { this.cast = cast; }

        public int getTotalCounted() { return totalCounted; }
        public void setTotalCounted(int totalCounted) { this.totalCounted = totalCounted; }

        public int getRejectedInvalid() { return rejectedInvalid; }
        public void setRejectedInvalid(int rejectedInvalid) { this.rejectedInvalid = rejectedInvalid; }

        public int getRejectedBlank() { return rejectedBlank; }
        public void setRejectedBlank(int rejectedBlank) { this.rejectedBlank = rejectedBlank; }

        public int getUncountedProxyCertificates() { return uncountedProxyCertificates; }
        public void setUncountedProxyCertificates(int uncountedProxyCertificates) { this.uncountedProxyCertificates = uncountedProxyCertificates; }

        public int getUncountedTooManyBallots() { return uncountedTooManyBallots; }
        public void setUncountedTooManyBallots(int uncountedTooManyBallots) { this.uncountedTooManyBallots = uncountedTooManyBallots; }

        public int getUncountedTooFewBallots() { return uncountedTooFewBallots; }
        public void setUncountedTooFewBallots(int uncountedTooFewBallots) { this.uncountedTooFewBallots = uncountedTooFewBallots; }

        public int getUncountedTakenAway() { return uncountedTakenAway; }
        public void setUncountedTakenAway(int uncountedTakenAway) { this.uncountedTakenAway = uncountedTakenAway; }

        public int getUncountedTooFewIssued() { return uncountedTooFewIssued; }
        public void setUncountedTooFewIssued(int uncountedTooFewIssued) { this.uncountedTooFewIssued = uncountedTooFewIssued; }

        public int getUncountedTooManyIssued() { return uncountedTooManyIssued; }
        public void setUncountedTooManyIssued(int uncountedTooManyIssued) { this.uncountedTooManyIssued = uncountedTooManyIssued; }

        public int getUncountedNoExplanation() { return uncountedNoExplanation; }
        public void setUncountedNoExplanation(int uncountedNoExplanation) { this.uncountedNoExplanation = uncountedNoExplanation; }

        public int getUncountedOther() { return uncountedOther; }
        public void setUncountedOther(int uncountedOther) { this.uncountedOther = uncountedOther; }

        public int getUncountedLost() { return uncountedLost; }
        public void setUncountedLost(int uncountedLost) { this.uncountedLost = uncountedLost; }

        public int getUncountedTooManyMail() { return uncountedTooManyMail; }
        public void setUncountedTooManyMail(int uncountedTooManyMail) { this.uncountedTooManyMail = uncountedTooManyMail; }

        public int getUncountedNoMail() { return uncountedNoMail; }
        public void setUncountedNoMail(int uncountedNoMail) { this.uncountedNoMail = uncountedNoMail; }

        @Override
        public String toString() {
            return "ElectionMetadata{" +
                    "cast=" + cast +
                    ", totalCounted=" + totalCounted +
                    ", rejectedInvalid=" + rejectedInvalid +
                    ", rejectedBlank=" + rejectedBlank +
                    ", uncountedProxyCertificates=" + uncountedProxyCertificates +
                    ", uncountedTooManyBallots=" + uncountedTooManyBallots +
                    ", uncountedTooFewBallots=" + uncountedTooFewBallots +
                    ", uncountedTakenAway=" + uncountedTakenAway +
                    ", uncountedTooFewIssued=" + uncountedTooFewIssued +
                    ", uncountedTooManyIssued=" + uncountedTooManyIssued +
                    ", uncountedNoExplanation=" + uncountedNoExplanation +
                    ", uncountedOther=" + uncountedOther +
                    ", uncountedLost=" + uncountedLost +
                    ", uncountedTooManyMail=" + uncountedTooManyMail +
                    ", uncountedNoMail=" + uncountedNoMail +
                    '}';
        }
}
