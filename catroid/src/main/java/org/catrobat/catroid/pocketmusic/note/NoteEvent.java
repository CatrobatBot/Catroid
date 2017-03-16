/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.pocketmusic.note;

import java.io.Serializable;

public class NoteEvent implements Serializable {

    private static final long serialVersionUID = 7483022549872527955L;

    private NoteName noteName;
    private boolean noteOn;

    public NoteEvent(NoteName noteName, boolean noteOn) {
        this.noteName = noteName;
        this.noteOn = noteOn;
    }

    public NoteEvent(NoteEvent noteEvent) {
        this.noteName = noteEvent.getNoteName();
        this.noteOn = noteEvent.isNoteOn();
    }

    public NoteName getNoteName() {
        return noteName;
    }

    public boolean isNoteOn() {
        return noteOn;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof NoteEvent)) {
            return false;
        }

        NoteEvent noteEvent = (NoteEvent) obj;

        return (noteName.equals(noteEvent.getNoteName())) && (noteOn == noteEvent.isNoteOn());
    }

    @Override
    public int hashCode() {
        int hashCode = 15;
        int primeWithGoodCollisionPrevention = 31;
        hashCode = primeWithGoodCollisionPrevention * hashCode + noteName.hashCode();
        return primeWithGoodCollisionPrevention * hashCode + (noteOn ? 1 : 0);
    }

    @Override
    public String toString() {
        return "[NoteEvent] noteName= " + noteName + " noteOn=" + noteOn;
    }
}
