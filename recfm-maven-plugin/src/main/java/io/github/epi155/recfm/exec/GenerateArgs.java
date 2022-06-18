package io.github.epi155.recfm.exec;

import lombok.Builder;
import lombok.RequiredArgsConstructor;


@Builder
@RequiredArgsConstructor
public class GenerateArgs {
    public final String sourceDirectory;
    public final String utilPackage;
    @Builder.Default
    public final int align = 4;
    @Builder.Default
    public final boolean doc = false;
    @Builder.Default
    public final boolean getCheck = true;
    @Builder.Default
    public final boolean setCheck = true;
}
