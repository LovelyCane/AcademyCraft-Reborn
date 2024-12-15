
Properties {
    VertexLayout {
    }
    Uniform {
        gSampler = sampler2D;
    }
}

Vertex {
#version 120

varying vec4 Color;
varying vec2 UV;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    Color = gl_Color;
    UV = gl_MultiTexCoord0.xy;
}
}

Fragment {
#version 120

varying vec4 Color;

uniform sampler2D gSampler;
varying vec2 UV;

void main() {
    gl_FragColor = Color * texture2D(gSampler, UV);
}
}