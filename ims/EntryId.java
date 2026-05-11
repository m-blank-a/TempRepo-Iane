package ims;

import ims.*;

public record EntryId(long transactionId, short subId, long position) {
	@Override
	public String toString() {
		return "%08X-%d".formatted(transactionId, subId);
	}
}